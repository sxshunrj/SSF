package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.MessageHead;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.util.NetUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/24 11:48
 */
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientChannelHandler.class);

    private AbstractTcpClientTransport tcpClientTransport;

    public ClientChannelHandler(AbstractTcpClientTransport tcpClientTransport) {
        this.tcpClientTransport = tcpClientTransport;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        try {
            if ( msg instanceof ResponseMessage ){
                ResponseMessage responseMessage = (ResponseMessage) msg;
                tcpClientTransport.handleResponse(responseMessage);
            } else if ( msg instanceof RequestMessage ){
                RequestMessage requestMessage = (RequestMessage) msg;
                if ( requestMessage.isCallbackRequestMsg()){
                    //TODO handle callback request
                } else {
                    Channel channel = ctx.channel();
                    throw new RpcException(String.format("the message should be callback request in channel %s ,but is:%s",
                            NetUtil.channelToString(channel.localAddress(), channel.remoteAddress()),
                            requestMessage),requestMessage);
                }
            } else if ( msg instanceof AbstractMessage ){
                throw new RpcException("error type of message", (AbstractMessage) msg);
            } else {
                throw new RpcException("the message is not ssf message");
            }
        } catch (Exception e){
            logger.error(e.getMessage(),e);
            if ( msg instanceof AbstractMessage ){
                throw RpcException.convertToRpcException((AbstractMessage) msg,e);
            } else {
                throw new RpcException("the message is not ssf message");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if ( cause instanceof RpcException ){
            RpcException rpcException = (RpcException) cause;
            final AbstractMessage message = rpcException.getMsg();
            if ( message != null && message.isCallbackRequestMsg() ){
                ResponseMessage callBackResponse = new ResponseMessage(false);
                callBackResponse.setException(cause);
                if ( message.getHead() != null ){
                    callBackResponse.setHead(message.getHead().clone());
                } else {
                    callBackResponse.setHead(new MessageHead());
                }
                callBackResponse.getHead().setMessageType(AbstractMessage.CALLBACK_RESPONSE_MSG);
                Future future = channel.writeAndFlush(callBackResponse);
                future.addListener(new FutureListener() {
                    @Override
                    public void operationComplete(Future future) throws Exception {
                        if ( future.isSuccess() ){
                            logger.debug("callback error message has been sent to server successfully:{}",message);
                        } else {
                            logger.error("callback error message has been sent to server failed:{}",message,future.cause());
                        }
                    }
                });
            }
        }
    }
}
