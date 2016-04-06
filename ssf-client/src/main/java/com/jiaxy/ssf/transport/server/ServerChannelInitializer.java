package com.jiaxy.ssf.transport.server;

import com.jiaxy.ssf.codec.MessageDecoderAdapter;
import com.jiaxy.ssf.config.ServerTransportConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/18 13:35
 */
@ChannelHandler.Sharable
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ServerTransportConfig serverTransportConfig;

    private ServerChannelHandler serverChannelHandler;


    public ServerChannelInitializer(ServerTransportConfig serverTransportConfig, ServerChannelHandler serverChannelHandler) {
        this.serverTransportConfig = serverTransportConfig;
        this.serverChannelHandler = serverChannelHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new MessageDecoderAdapter(serverChannelHandler,serverTransportConfig));

    }
}
