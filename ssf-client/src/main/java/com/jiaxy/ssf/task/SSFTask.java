package com.jiaxy.ssf.task;

import com.jiaxy.ssf.codec.protocol.Protocol;
import com.jiaxy.ssf.codec.protocol.ProtocolFactory;
import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.common.StringUtil;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.*;
import com.jiaxy.ssf.processor.CallbackProcessor;
import com.jiaxy.ssf.processor.Processor;
import com.jiaxy.ssf.processor.ProcessorManager;
import com.jiaxy.ssf.proxy.ProxyType;
import com.jiaxy.ssf.proxy.ServiceProxyFactory;
import com.jiaxy.ssf.service.Callback;
import com.jiaxy.ssf.transport.ByteBufAllocatorHolder;
import com.jiaxy.ssf.transport.client.ClientTransport;
import com.jiaxy.ssf.util.ResponseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static com.jiaxy.ssf.processor.ProcessorManagerFactory.*;
import static com.jiaxy.ssf.util.Callbacks.*;
import static com.jiaxy.ssf.util.NetUtil.*;
import static com.jiaxy.ssf.transport.client.ClientTransportFactory.*;

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
                writeAndFlush(channel, responseMessage, true);
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
            ProcessorManager processorManager = getInstance();
            Processor<RequestMessage,ResponseMessage> processor = processorManager.getProcessor(processorKey(serviceName, alias));
            if ( processor == null ){
                throw new RpcException(String.format("can't found a processor for %s:%s ,alias:%s.maybe the service has not been loaded or no this service export in channel",
                        channelToString(channel),
                        serviceName, alias));
            }
            if (isCallbackMethod(serviceName,methodName)){
                handleCallbackRequest(requestMessage, processorManager, channel);
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
                                channelToString(channel),
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


    private CallbackProcessor registerCallbackProcessor(ProcessorManager processorManager,String callbackInstanceId,Channel channel){
        String host = ipString((InetSocketAddress) channel.remoteAddress());
        int port = port((InetSocketAddress) channel.remoteAddress());
        ClientTransportKey key = buildKey(ProtocolType.SSF,host,port);
        ClientTransport clientTransport = getClientTransport(key,channel);
        CallbackProcessor callbackProcessor = new CallbackProcessor(clientTransport,callbackInstanceId);
        processorManager.register(callbackInstanceId,callbackProcessor);
        return callbackProcessor;

    }

    private CallbackProcessor getCallbackProcessor(ProcessorManager processorManager,String callbackInstanceId,Channel channel){
        CallbackProcessor callbackProcessor = (CallbackProcessor) processorManager.getProcessor(callbackInstanceId);
        if (callbackProcessor == null){
            return registerCallbackProcessor(processorManager,callbackInstanceId,channel);
        } else {
            return callbackProcessor;
        }
    }



    private void handleCallbackRequest(RequestMessage requestMessage,ProcessorManager processorManager,Channel channel){
        String callbackInstanceId = (String)requestMessage.getHead().getAttrValue(MessageHead.HeadKey.CALLBACK_INSTANCE_ID.getKey());
        if (StringUtil.isEmpty(callbackInstanceId)){
            throw new IllegalArgumentException("callback instance id is empty");
        }
        CallbackProcessor callbackProcessor = getCallbackProcessor(processorManager,callbackInstanceId,channel);
        Callback callback = ServiceProxyFactory.getCallbackProxy(ProxyType.JDK,
                callbackInstanceId,
                callbackProcessor);
        Class[] argTypeClasses = requestMessage.getRequestMessageBody().getArgsTypeClasses();
        int callbackParamIndex = -1;
        for ( int i = 0 ;i < argTypeClasses.length ;i++){
            if (isCallback(argTypeClasses[i])){
                callbackParamIndex = i;
                break;
            }
        }
        if (callbackParamIndex == -1){
            throw new IllegalArgumentException("could not find callback param index in the params");
        }
        Object[] objArr = requestMessage.getRequestMessageBody().getArgs();
        objArr[callbackParamIndex] = callback;
        requestMessage.getRequestMessageBody().setArgs(objArr);
    }

}
