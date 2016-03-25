package com.jiaxy.ssf.transport;

import com.jiaxy.ssf.common.Constants;
import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.config.ServerTransportConfig;
import com.jiaxy.ssf.thread.NamedThreadFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     相关EventLoop 线程池的创建
 *
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/17 15:36
 */
public class EventLoopFactory {

    private static EventLoopGroup bossEventLoopGroup;

    private static EventLoopGroup workerEventLoopGroup;

    private static EventLoopGroup clientEventLoopGroup;


    /**
     *
     * @param config
     * @return singleton boos event loop group
     */
    public static EventLoopGroup getSharedBossEventLoopGroup(ServerTransportConfig config){
        if ( bossEventLoopGroup == null || bossEventLoopGroup.isShutdown() ){
           synchronized (EventLoopFactory.class){
               if ( bossEventLoopGroup == null ){
                   bossEventLoopGroup = createBossEventLoopGroup(config);
               }
           }
        }
        return bossEventLoopGroup;
    }

    public static EventLoopGroup getSharedWorkerEventLoopGroup(ServerTransportConfig config){
        if ( workerEventLoopGroup == null || workerEventLoopGroup.isShutdown() ){
           synchronized (EventLoopFactory.class){
               if ( workerEventLoopGroup == null ){
                   workerEventLoopGroup = createWorkerEventLoopGroup(config);
               }
           }
        }
        return workerEventLoopGroup;
    }


    public static EventLoopGroup getSharedClientEventLoopGroup(ClientTransportConfig config){
        if ( clientEventLoopGroup == null ){
            synchronized (EventLoopFactory.class){
                if ( clientEventLoopGroup == null || clientEventLoopGroup.isShutdown() ){
                    clientEventLoopGroup = createClientEventLoopGroup(config);
                }
            }
        }
        return clientEventLoopGroup;
    }

    public static EventLoopGroup createBossEventLoopGroup(ServerTransportConfig config){
        int threads = config.getBossNioEventThreads();
        if ( config.getBossNioEventThreads() == 0 ){
           threads = Math.max(4, Constants.CPU_CORES / 2);
        }
        NamedThreadFactory threadFactory = new NamedThreadFactory("SSF-BOSS-");
        EventLoopGroup eventLoopGroup;
        if ( config.isEpoll() ){
            eventLoopGroup = new EpollEventLoopGroup(threads,threadFactory);
        } else {
            eventLoopGroup = new NioEventLoopGroup(threads,threadFactory);
        }
        return eventLoopGroup;
    }


    public static EventLoopGroup createWorkerEventLoopGroup(ServerTransportConfig config){
        int threads = config.getWorkerNioEventThreads();
        if ( threads == 0 ){
            threads = Math.max(8,Constants.CPU_CORES + 1);
        }
        NamedThreadFactory threadFactory = new NamedThreadFactory("SSF-WORKER-");
        EventLoopGroup eventLoopGroup;
        if ( config.isEpoll() ){
            eventLoopGroup = new EpollEventLoopGroup(threads,threadFactory);
        } else {
            eventLoopGroup = new NioEventLoopGroup(threads,threadFactory);
        }
        return eventLoopGroup;
    }

    public static EventLoopGroup createClientEventLoopGroup(ClientTransportConfig config){
        int threads = config.getChildNioEventThreads();
        if ( threads == 0 ){
            threads = Math.max(6,Constants.CPU_CORES + 1);
        }
        NamedThreadFactory threadFactory = new NamedThreadFactory("SSF-Client-WORKER");
        EventLoopGroup eventLoopGroup;
        if ( config.isEpoll() ){
            eventLoopGroup = new EpollEventLoopGroup(threads,threadFactory);
        } else {
            eventLoopGroup = new NioEventLoopGroup(threads,threadFactory);
        }
        return eventLoopGroup;
    }
}
