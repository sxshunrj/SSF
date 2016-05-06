package com.jiaxy.ssf.transport.server;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.config.ServerTransportConfig;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.MessageHead;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.processor.Processor;
import com.jiaxy.ssf.processor.ProcessorManager;
import com.jiaxy.ssf.processor.TaskProcessor;
import com.jiaxy.ssf.task.RPCTask;
import com.jiaxy.ssf.task.SSFTask;
import com.jiaxy.ssf.util.NetUtil;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.jiaxy.ssf.processor.ProcessorManagerFactory.getInstance;
import static com.jiaxy.ssf.processor.ProcessorManagerFactory.processorKey;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/25 18:24
 */
@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class);


    private ServerTransportConfig serverTransportConfig;

    public ServerChannelHandler(ServerTransportConfig serverTransportConfig) {
        this.serverTransportConfig = serverTransportConfig;
        ProcessorManager processorManager = getInstance();
        processorManager.register(processorKey(serverTransportConfig.getHost(),serverTransportConfig.getPort()),new TaskProcessor(serverTransportConfig));

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
        ProcessorManager processorManager = getInstance();
        Processor<RPCTask,Void> processor =  processorManager.getProcessor(processorKey(serverTransportConfig.getHost(),
                serverTransportConfig.getPort()));
        try {
            processor.execute(getTask(ctx.channel(), (AbstractMessage) msg));
        } catch (Throwable throwable) {
            throw RpcException.convertToRpcException(throwable);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if ( cause instanceof IOException ){
            logger.warn("IOException:{}:{}",
                    NetUtil.channelToString(channel.localAddress(), channel.remoteAddress()),
                    cause.getMessage()
            );
        } else if ( cause instanceof RpcException ){
            ResponseMessage responseMessage = new ResponseMessage(false);
            RpcException rpcException = (RpcException) cause;
            AbstractMessage sourceMessage = rpcException.getMsg();
            if ( sourceMessage != null && sourceMessage.getHead() != null ){
                responseMessage.setHead(sourceMessage.getHead().clone());
                responseMessage.getHead().setMessageType(AbstractMessage.RESPONSE_MSG);
            } else {
                MessageHead head = new MessageHead();
                head.setMessageType(AbstractMessage.RESPONSE_MSG);
                responseMessage.setHead(head);
            }
            responseMessage.setException(rpcException);
            ChannelFuture channelFuture = ctx.writeAndFlush(responseMessage);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if ( !future.isSuccess() ){
                        logger.error("write error response message back failed:",future.cause());
                    }

                }
            });
        } else {
             logger.warn("{}:{}:{}",
                     new Object[]{cause.getClass().getName(),
                     NetUtil.channelToString( channel.localAddress(),channel.remoteAddress()),
                     cause.getMessage()}
             );
        }
    }



    private RPCTask getTask(Channel channel,AbstractMessage message){
        ProtocolType protocolType = ProtocolType.valueOf(message.getHead().getProtocolType());
        RPCTask task = null;
        switch ( protocolType ){
            case SSF:
                task = new SSFTask(channel,message);
                break;
            default:
                throw new RpcException(String.format("unknown protocol:%s",protocolType));
        }
        return task;
    }
}
