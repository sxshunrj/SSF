package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.codec.protocol.ssf.ByteToSSFMessageDecoder;
import com.jiaxy.ssf.codec.protocol.ssf.SSFMessageToByteEncoder;
import com.jiaxy.ssf.config.ClientTransportConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/24 13:21
 */
@ChannelHandler.Sharable
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{

    private ClientTransportConfig clientTransportConfig;

    private ClientChannelHandler clientChannelHandler;

    public ClientChannelInitializer(ClientTransportConfig clientTransportConfig,ClientChannelHandler clientChannelHandler) {
        this.clientChannelHandler = clientChannelHandler;
        this.clientTransportConfig = clientTransportConfig;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new SSFMessageToByteEncoder())
                .addLast(new ByteToSSFMessageDecoder(clientTransportConfig.getPayload()))
                .addLast(clientChannelHandler);
    }
}
