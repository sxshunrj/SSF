package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.codec.protocol.Protocol;
import com.jiaxy.ssf.codec.protocol.ProtocolFactory;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.*;
import com.jiaxy.ssf.service.Callback;
import com.jiaxy.ssf.thread.dreamwork.AbstractDreamTask;
import com.jiaxy.ssf.util.Callbacks;
import com.jiaxy.ssf.util.NetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
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
                    handleCallbackRequest(ctx.channel(),requestMessage);
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


    private void handleCallbackRequest(final Channel channel,final RequestMessage requestMessage){
        String callbackInstanceId = (String) requestMessage.getHead().getAttrValue(MessageHead.HeadKey.CALLBACK_INSTANCE_ID.getKey());
        final Callback callback = Callbacks.getCallbackInstance(callbackInstanceId);
        final ResponseMessage responseMessage = new ResponseMessage(false);
        Callbacks.getCallbackDreamwork().execute(new AbstractDreamTask() {
            @Override
            public void run() {
                Protocol protocol = ProtocolFactory.getProtocol(requestMessage.getProtocolType(), requestMessage.getCodecType());
                ByteBuf msgBodyBuf = requestMessage.getMsgBodyBuf();
                try {
                    RequestMessageBody requestMessageBody = protocol.decode(requestMessage.getMsgBodyBuf(), RequestMessageBody.class);
                    requestMessage.setRequestMessageBody(requestMessageBody);
                    Object callbackResponse = callback.callback(requestMessageBody.getArgs()[0]);
                    responseMessage.setResponse(callbackResponse);
                } catch (Exception e){
                    logger.error("handle callback request error in channel:{}",NetUtil.channelToString(channel.remoteAddress(),channel.localAddress()),e);
                    responseMessage.setException(RpcException.convertToRpcException(requestMessage,e));
                } finally {
                    msgBodyBuf.release();
                }
                responseMessage.setHead(requestMessage.getHead());
                responseMessage.getHead().setMessageType(AbstractMessage.CALLBACK_RESPONSE_MSG);
                Future future = channel.writeAndFlush(responseMessage);
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()){
                            logger.error("send callback response message error.in channel {}",
                                    NetUtil.channelToString(channel.remoteAddress(),channel.localAddress()),future.cause());
                            throw new RpcException("send callback response message error.",future.cause());
                        }
                    }
                });
            }
        });

    }
}
