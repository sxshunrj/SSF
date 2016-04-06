package com.jiaxy.ssf.thread.dreamwork.assist;


import com.jiaxy.ssf.thread.dreamwork.DreamTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Title:<br>
 * Desc:<br>
 * <p>
 * </p>
 *
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2015/05/16 20:43
 */
public class ThresholdUtil {

    private static Logger logger = LoggerFactory.getLogger(ThresholdUtil.class);


    public static final String TASK_NUM = "taskNum_";


    public static final String DEFAULT_TASK_NUM = "server.biz-pool.defaultTaskNum";

    public static final String TASK_TIMEOUT= ".timeout";

    public static final int DEFAULT_TASK_TIMEOUT = 5000;

    // -1 代表不限制
    private static int taskNumThreshold = -1;

    private static Map<String,Integer> taskNumThresholdMap = new HashMap<String, Integer>();


    /**
     *
     *  this task num run parallel
     *
     * @param dream
     *
     * @return
     */
    public static int getTaskNumThreshold(String dream){
        Integer taskNum = taskNumThresholdMap.get(TASK_NUM+dream);
        if (  taskNum == null ){
            return taskNumThreshold;
        } else {
            return taskNum;
        }
    }


    /**
     *
     * wait timeout
     *
     * @param dream
     *
     * @return
     */
    public static int getTaskTimeoutThreshold(String dream){
        //default timeout
        return DEFAULT_TASK_TIMEOUT;
    }

    /**
     *
     * @param dreamTask
     *
     * @return true if this task time out
     */
    public static boolean isTimeout(DreamTask dreamTask){
        long waitTime = System.currentTimeMillis() - dreamTask.createdTime();
        if ( waitTime > dreamTask.timeout()){
            return true;
        } else {
            return false;
        }
    }

}
