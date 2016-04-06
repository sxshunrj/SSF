package com.jiaxy.ssf.thread.dreamwork;

import com.jiaxy.ssf.config.ServerTransportConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/06 11:43
 */
public class DreamworkFactory {

    /**
     * key : server port
     */
    private final static ConcurrentHashMap<Integer,Dreamwork> dreamworks = new ConcurrentHashMap<Integer, Dreamwork>();


    public static void initDreamwork(ServerTransportConfig serverTransportConfig){
        Integer serverPort = serverTransportConfig.getPort();
        Dreamwork dreamwork = getDreamwork(serverPort);
        if ( dreamwork == null ){
            synchronized ( serverPort ){
                if ( dreamwork == null ){
                    dreamwork = buildDreamwork(serverTransportConfig);
                    dreamworks.put(serverPort,dreamwork);
                }
            }
        }
    }


    public static Dreamwork getDreamwork(Integer serverPort){
       return dreamworks.get(serverPort);
    }


    public static void shutdown(Integer serverPort){
        Dreamwork dreamwork = getDreamwork(serverPort);
        if ( dreamwork != null ){
            dreamwork.shutdownNow();
        }
    }


    private static Dreamwork buildDreamwork(ServerTransportConfig serverTransportConfig){
        //TODO pool param can config
        Dreamwork dreamwork = new Dreamwork(true,
                20,
                serverTransportConfig.getBizPoolSize(),
                60000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque(1000),
                true
                );
        return dreamwork;
    }

}
