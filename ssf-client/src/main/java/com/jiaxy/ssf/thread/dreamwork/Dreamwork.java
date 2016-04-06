package com.jiaxy.ssf.thread.dreamwork;

import com.jiaxy.ssf.thread.NamedThreadFactory;
import com.jiaxy.ssf.thread.dreamwork.assist.IncubatorStat;
import com.jiaxy.ssf.thread.dreamwork.assist.ThresholdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.jiaxy.ssf.thread.dreamwork.assist.ThresholdUtil.*;

/**
 * Title:<br>
 * Desc:<br>
 * <p>
 *     thread pool based on {@link java.util.concurrent.ThreadPoolExecutor}
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2015/05/16 17:27
 */
public class Dreamwork {

    private Logger logger = LoggerFactory.getLogger(Dreamwork.class);

    private IncubatorStat stat = new IncubatorStat();

    private ConcurrentHashMap<String,ConcurrentLinkedQueue<DreamTask>> waitTaskQueueMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<DreamTask>>();

    private ExecutorService awaitTaskConsumer = Executors.newSingleThreadExecutor(new NamedThreadFactory("AwaitTaskConsumer",true));

    private ScheduledExecutorService checkService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("AwaitCheck",true));

    private ThreadPoolExecutor dwpool = null;

    public Dreamwork(boolean isDream,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, final BlockingQueue<Runnable> workQueue,boolean daemon) {
        if ( isDream ){
            dwpool = new Incubator(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,daemon);
            addResourceAvailableListener();
            checkResourceAvailable();
        } else {
            dwpool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue);
        }
    }

    public Dreamwork(boolean isDream,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        if ( isDream ){
            dwpool = new Incubator(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
            addResourceAvailableListener();
            checkResourceAvailable();
        } else {
            dwpool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue,threadFactory);
        }
    }

    public Dreamwork(boolean isDream,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        if ( isDream ){
            dwpool = new Incubator(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler,true);
            addResourceAvailableListener();
            checkResourceAvailable();
        } else {
            dwpool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue,handler);
        }
    }

    public Dreamwork(boolean isDream,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        if ( isDream ){
            dwpool = new Incubator(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
            addResourceAvailableListener();
            checkResourceAvailable();
        } else {
            dwpool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue,threadFactory,handler);
        }
    }


    /**
     * delegate this task to dwpool (ThreadPoolExecutor)
     *
     * @param dreamTask the need execute task
     */
    public void execute(DreamTask dreamTask){
        dwpool.execute(dreamTask);
    }


    public int getPoolSize(){
        return dwpool.getPoolSize();
    }

    public int getActiveCount(){
        return dwpool.getActiveCount();
    }

    public int getLargestPoolSize() {
        return dwpool.getLargestPoolSize();
    }



    public void shutdown() {
        dwpool.shutdown();
    }


    public List<Runnable> shutdownNow() {
        return dwpool.shutdownNow();
    }


    public void dumpWaitTask(){

    }

    public String showWaitTaskNum(){
        StringBuilder sb = new StringBuilder("[");
        for (Map.Entry<String,ConcurrentLinkedQueue<DreamTask>> entry : waitTaskQueueMap.entrySet()){
            sb.append("{\"time\":")
                    .append(new Date().getTime())
                    .append(",")
                    .append("\"dream\":")
                    .append("\"")
                    .append(entry.getKey())
                    .append("\"")
                    .append(",")
                    .append("\"dreamTask\":")
                    .append(entry.getValue().size())
                    .append("}")
                    .append(",");

        }
        sb.append("]");
        System.out.println(sb.toString());
        return sb.toString();
    }

    public String showIncubatorStatus(){
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"active\":")
                .append(dwpool.getActiveCount())
                .append(",")
                .append("\"poolsize\":")
                .append(dwpool.getPoolSize())
                .append(",")
                .append("\"task\":")
                .append(dwpool.getTaskCount())
                .append(",")
                .append("\"largestpoolsize\":")
                .append(dwpool.getLargestPoolSize());
        sb.append("}");
        return sb.toString();
    }

    /**
     */
    private void addResourceAvailableListener() {
        ((Incubator) dwpool).addThreadAvailableListener(new ThreadAvailableListener() {
            @Override
            public void retryDreamTask(final String dream) {
                awaitTaskConsumer.execute(new Runnable() {
                    @Override
                    public void run() {
                        //retry
                        ConcurrentLinkedQueue<DreamTask> waitQueue = waitTaskQueueMap.get(dream);
                        if (waitQueue != null) {
                            DreamTask dreamTask = waitQueue.poll();
                            if (dreamTask != null) {
                                if (!isTimeout(dreamTask)) {
                                    logger.debug("retry execute :" + dreamTask.dream());
                                    Dreamwork.this.execute(dreamTask);
                                } else {
                                    logger.info("dropped for timeout :" + dreamTask.dream());
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private void checkResourceAvailable(){
        checkService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<String, ConcurrentLinkedQueue<DreamTask>> entry : waitTaskQueueMap.entrySet()) {
                    if (stat.isExecutable(entry.getKey())) {
                        DreamTask dreamTask = entry.getValue().poll();
                        if (dreamTask != null) {
                            if (!isTimeout(dreamTask)) {
                                logger.info("retry execute :" + dreamTask.dream() + " by check service");
                                //just only one
                                Dreamwork.this.execute(dreamTask);
                            } else {
                                logger.info("the dream task was dropped for timeout :" + dreamTask.dream() + " by check service");
                            }
                        }
                    }
                }
            }
        }, DEFAULT_TASK_TIMEOUT - 500, DEFAULT_TASK_TIMEOUT - 500, TimeUnit.MILLISECONDS);


    }

    /**
     * thread pool based on {@link java.util.concurrent.ThreadPoolExecutor}
     */
    class Incubator extends ThreadPoolExecutor{

        private NamedThreadFactory defaultThreadFactory = new NamedThreadFactory("Incubator",true);

        private DreamRejectedExecutionHandler dreamRejectedExecutionHandler = new DreamRejectedExecutionHandler();

        private ThreadAvailableListener listener = null;

        public Incubator(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,boolean daemon) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
            defaultThreadFactory.setDaemon(daemon);
            setThreadFactory(defaultThreadFactory);
            setRejectedExecutionHandler(dreamRejectedExecutionHandler);

        }

        public Incubator(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        public Incubator(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler,boolean daemon) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
            defaultThreadFactory.setDaemon(daemon);
            setThreadFactory(defaultThreadFactory);
            setRejectedExecutionHandler(dreamRejectedExecutionHandler);
        }

        public Incubator(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        public void addThreadAvailableListener(ThreadAvailableListener listener){
            this.listener = listener;
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            DreamTask dreamTask = (DreamTask) r;
            if ( !stat.isExecutable(dreamTask.dream()) ){
                ConcurrentLinkedQueue waitTaskQueue = waitTaskQueueMap.get(dreamTask.dream());
                if ( waitTaskQueue == null ){
                    waitTaskQueueMap.putIfAbsent(dreamTask.dream(), new ConcurrentLinkedQueue<DreamTask>());
                    waitTaskQueueMap.get(dreamTask.dream()).offer(dreamTask);
                } else {
                    waitTaskQueue.offer(dreamTask);
                }
                logger.debug(dreamTask.dream() + " is not allowed to execute");
                throw new DreamRejectException(dreamTask.dream()+" exceed the threshold of task num:"+ ThresholdUtil.getTaskNumThreshold(dreamTask.dream()));
            }
            stat.add(dreamTask.dream());
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            DreamTask dreamTask = (DreamTask) r;
            super.afterExecute(r, t);
            stat.del(dreamTask.dream());
            listener.retryDreamTask(dreamTask.dream());
        }
    }

    interface ThreadAvailableListener {

        void retryDreamTask(String dream);

    }

    class DreamRejectedExecutionHandler implements RejectedExecutionHandler {

        private int i = 1;

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (i++ % 7 == 0) {
                i = 1;
                logger.warn("Task:{} has been reject for ThreadPool exhausted!" +
                                " pool:{}, active:{}, queue:{}, task count: {}",
                        new Object[]{
                                r,
                                executor.getPoolSize(),
                                executor.getActiveCount(),
                                executor.getQueue().size(),
                                executor.getTaskCount()
                        });
            }
            throw new RejectedExecutionException("Thread pool has bean exhausted");
        }
    }
}
