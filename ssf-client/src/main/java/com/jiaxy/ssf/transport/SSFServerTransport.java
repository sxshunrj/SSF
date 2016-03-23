package com.jiaxy.ssf.transport;

import com.jiaxy.ssf.common.Constants;
import com.jiaxy.ssf.config.ServerTransportConfig;
import com.jiaxy.ssf.exception.InitException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/17 15:03
 */
public class SSFServerTransport implements ServerTransport {

    private Logger logger = LoggerFactory.getLogger(SSFServerTransport.class);

    private ServerTransportConfig serverTransportConfig;

    public SSFServerTransport(ServerTransportConfig serverTransportConfig) {
        this.serverTransportConfig = serverTransportConfig;
    }

    @Override
    public boolean start() {
        logger.info("SSF Server transport start");
        Class clz;
        if ( serverTransportConfig.isEpoll() ){
            clz = EpollServerSocketChannel.class;
        } else {
            clz = NioServerSocketChannel.class;
        }
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(EventLoopFactory.getSharedBossEventLoopGroup(serverTransportConfig),
                              EventLoopFactory.getSharedWorkerEventLoopGroup(serverTransportConfig))
                .channel(clz)
                .childHandler(new ServerChannelInitializer())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,serverTransportConfig.getConnectionTimeout())
                .option(ChannelOption.SO_BACKLOG,serverTransportConfig.getBACKLOG())
                .option(ChannelOption.SO_REUSEADDR, !Constants.WINDOWS)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE,serverTransportConfig.isKeepAlive())
                .childOption(ChannelOption.TCP_NODELAY,serverTransportConfig.isTcpNoDelay())
                .childOption(ChannelOption.ALLOCATOR,new UnpooledByteBufAllocator(false))
                .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK,32 * 1024)
                .childOption(ChannelOption.SO_RCVBUF,1024 * 1024)
                .childOption(ChannelOption.SO_SNDBUF,1024 * 1024)
                .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
        ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(serverTransportConfig.getHost(),serverTransportConfig.getPort()));
        ChannelFuture channelFuture = future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if ( future.isSuccess() ){
                    logger.info(" SSF Server has bind to {}:{} successfully.",serverTransportConfig.getHost(),serverTransportConfig.getPort());
                } else {
                    logger.error(" SSF Server bind to {}:{} failed.", serverTransportConfig.getHost(), serverTransportConfig.getPort());
                    EventLoopFactory.getSharedBossEventLoopGroup(serverTransportConfig).shutdownGracefully();
                    EventLoopFactory.getSharedWorkerEventLoopGroup(serverTransportConfig).shutdownGracefully();
                    throw new InitException("SS Server start failed:",future.cause());
                }
            }
        });

        try {
            channelFuture.await(5000, TimeUnit.MICROSECONDS);
            if ( channelFuture.isSuccess() ){
                return true;
            }
        } catch (InterruptedException e) {
            logger.error("SSF Server await the bind result error",e);
            return false;
        }
        return false;
    }

    @Override
    public boolean stop() {
        logger.info("stop the SSF Server transport...");
        Future bossShutdownFuture = EventLoopFactory.getSharedBossEventLoopGroup(serverTransportConfig).shutdownGracefully();
        Future workerShutdownFuture = EventLoopFactory.getSharedWorkerEventLoopGroup(serverTransportConfig).shutdownGracefully();
        boolean rs = true;
        try {
            bossShutdownFuture.await(5000,TimeUnit.MILLISECONDS);
            workerShutdownFuture.await(5000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("SSF Server await the boss shutdown error",e);
        }
        if ( !bossShutdownFuture.isSuccess()){
            rs = false;
            logger.error(" SSF Server boss event loop shutdown failed:",bossShutdownFuture.cause());
        }
        if ( !workerShutdownFuture.isSuccess() ){
            rs = false;
            logger.error(" SSF Server worker event loop shutdown failed:",workerShutdownFuture.cause());
        }
        return rs;
    }
}
