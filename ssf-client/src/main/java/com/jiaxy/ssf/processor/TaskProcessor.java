package com.jiaxy.ssf.processor;

import com.jiaxy.ssf.config.ServerTransportConfig;
import com.jiaxy.ssf.task.RPCTask;
import com.jiaxy.ssf.thread.dreamwork.Dreamwork;
import com.jiaxy.ssf.thread.dreamwork.DreamworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jiaxy.ssf.processor.ProcessorManagerFactory.*;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/05 18:27
 */
public class TaskProcessor implements Processor<RPCTask,Void>{

    private static final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);


    private ServerTransportConfig serverTransportConfig;

    private Dreamwork dreamwork;

    public TaskProcessor(ServerTransportConfig serverTransportConfig) {
        this.serverTransportConfig = serverTransportConfig;
        if ( serverTransportConfig != null ){
            dreamwork = DreamworkFactory.getDreamwork(this.serverTransportConfig.getPort());
        }
    }

    @Override
    public Void execute(RPCTask task) {
        if ( dreamwork != null ){
            dreamwork.execute(task);
        } else {
            logger.error("the thread pool of the {} is null.so that task was not executed",
                    processorKey(this.serverTransportConfig.getHost(), this.serverTransportConfig.getPort()));
        }
        return null;
    }




}
