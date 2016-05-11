package com.jiaxy.ssf.task;

import com.jiaxy.ssf.codec.protocol.Protocol;
import com.jiaxy.ssf.codec.protocol.ProtocolFactory;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.*;
import com.jiaxy.ssf.processor.Processor;
import com.jiaxy.ssf.processor.ProcessorManager;
import com.jiaxy.ssf.transport.ByteBufAllocatorHolder;
import com.jiaxy.ssf.util.NetUtil;
import com.jiaxy.ssf.util.ResponseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
 * @since 2016/04/06 14:00
 */
public class SSFTask extends RPCTask {

    private static final Logger logger = LoggerFactory.getLogger(SSFTask.class);


    private Channel channel;


    private AbstractMessage message;

    public SSFTask(Channel channel, AbstractMessage message) {
        this.channel = channel;
        this.message = message;
    }

    @Override
    public void run() {
        if ( message instanceof RequestMessage ){
            RequestMessage requestMessage = (RequestMessage) message;
            ResponseMessage responseMessage = null;
            if ( requestMessage.isHeartBeatRequestMsg() ){
                responseMessage = MessageBuilder.buildHeartbeatResponse(requestMessage);
                NetUtil.writeAndFlush(channel,responseMessage,true);
                return;
            } else {
                handleRequest(requestMessage);
            }
        } else {
            logger.error("unknown message type . msg :{}",message);
            throw new RpcException("unknown message type");
        }
    }


    private void handleRequest(RequestMessage requestMessage){
        try {
            long current = System.currentTimeMillis();
            Integer timeout = requestMessage.getCallTimeout();
            if ( timeout != null && timeout >= 0 ){
                if ( current - requestMessage.getReceivedTime() > timeout ){
                    logger.warn("the request message is timeout:{}",requestMessage);
                }
            }
            Protocol protocol = ProtocolFactory.getProtocol(requestMessage.getProtocolType(),
                    requestMessage.getCodecType());
            RequestMessageBody reqBody = protocol.decode(requestMessage.getMsgBodyBuf(),RequestMessageBody.class);
            requestMessage.setRequestMessageBody(reqBody);
            String serviceName = requestMessage.getServiceName();
            String methodName = requestMessage.getMethodName();
            String alias = requestMessage.getAlias();
            //TODO handle callback
            ProcessorManager processorManager = getInstance();
            Processor<RequestMessage,ResponseMessage> processor = processorManager.getProcessor(processorKey(serviceName, alias));
            if ( processor == null ){
                throw new RpcException(String.format("can't found a processor for %s:%s ,alias:%s.maybe the service has not been loaded or no this service export in channel",
                        NetUtil.channelToString(channel),
                        serviceName,alias));
            }
            ResponseMessage responseMessage = processor.execute(requestMessage);
            ByteBuf buf = ByteBufAllocatorHolder.getBuf();
            buf = protocol.encode(responseMessage,buf);
            responseMessage.setMsgBuf(buf);
            ChannelFuture channelFuture = channel.writeAndFlush(responseMessage);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if ( !future.isSuccess() ){
                        logger.error("send response to client error in {}.Cause by:",
                                NetUtil.channelToString(channel),
                                future.cause()
                        );
                    }
                }
            });
        } catch (Throwable e){
            ResponseUtil.sendExceptionMessage(e,requestMessage,channel);
        } finally {
            ByteBuf buf = requestMessage.getMsgBuf();
            if ( buf != null ){
                buf.release();
            }
        }
    }

    private void handleCallbackRequest(RequestMessage requestMessage){

    }

}
