package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.codec.protocol.Protocol;
import com.jiaxy.ssf.codec.protocol.ProtocolFactory;
import com.jiaxy.ssf.common.CodecType;
import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.exception.InitException;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.MsgFuture;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.transport.ByteBufAllocatorHolder;
import com.jiaxy.ssf.transport.EventLoopFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
 * @since 2016/03/22 11:17
 */
public class SSFClientTransport extends AbstractTcpClientTransport {

    private Logger logger = LoggerFactory.getLogger(SSFClientTransport.class);

    private Channel channel;


    public SSFClientTransport(String ip, int port, ClientTransportConfig clientTransportConfig) {
        super(ip, port, clientTransportConfig);
    }


    @Override
    MsgFuture doSendAsync(RequestMessage msg, int timeout) {
        if ( msg == null ){
            throw new RpcException("request message is null",msg);
        }
        MsgFuture<ResponseMessage> msgFuture = new MsgFuture<ResponseMessage>(channel,timeout,msg);
        addMsgFuture(msg,msgFuture);
        //TODO callback request
        Protocol protocol = ProtocolFactory.getProtocol(ProtocolType.valueOf(msg.getHead().getProtocolType()),
                CodecType.valueOf(msg.getHead().getCodecType()));
        ByteBuf buf = ByteBufAllocatorHolder.getBuf();
        //encode
        protocol.encode(msg,buf);
        msg.setMsgBuf(buf);
        channel.writeAndFlush(msg,channel.voidPromise());
        msgFuture.setSendTime(System.currentTimeMillis());
        return msgFuture;
    }

    @Override
    public void connect() {
        open(clientTransportConfig);
    }

    @Override
    public void disConnect() {
        if ( isConnected()){
            try {
                channel.close();
            } catch (Exception e){
                logger.error("SSFClientTransport disConnect error",e);
            }
        }

    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.isActive() && channel.isOpen();
    }

    private Channel open(ClientTransportConfig clientTransportConfig){
        if ( isConnected() ){
            return channel;
        }
        EventLoopGroup eventLoopGroup = EventLoopFactory.getSharedClientEventLoopGroup(clientTransportConfig);
        Class clz = NioSocketChannel.class;
        if ( clientTransportConfig.isEpoll() ){
            clz = EpollSocketChannel.class;
        }
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(clz)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK,32 * 1024)
                    .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK,8 * 1024)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
            ClientChannelHandler clientChannelHandler = new ClientChannelHandler(this);
            ClientChannelInitializer clientChannelInitializer = new ClientChannelInitializer(clientTransportConfig,clientChannelHandler);
            bootstrap.handler(clientChannelInitializer);
            ChannelFuture channelFuture = bootstrap.connect(remoteIP,remotePort);
            channelFuture.awaitUninterruptibly(clientTransportConfig.getConnectionTimeout(), TimeUnit.MILLISECONDS);
            if ( channelFuture.isSuccess() ){
                channel = channelFuture.channel();
                setLocalAddress((InetSocketAddress) channel.localAddress());
                setRemoteAddress((InetSocketAddress) channel.remoteAddress());
                //TODO remote address maybe the same with local address
            } else {
                throw new InitException("Failed to connect :"+remoteIP+":"+remotePort,channelFuture.cause());
            }

        } catch (InitException e){
            throw e;
        } catch (Exception e){
            throw new InitException("Failed to connect :"+remoteIP+":"+remotePort,e);
        }
        return channel;
    }


}
