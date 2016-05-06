package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.exception.ClientTimeoutException;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.MsgFuture;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jiaxy.ssf.message.AbstractMessage.*;
/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     the tcp connection
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 17:51
 */
public abstract class AbstractTcpClientTransport extends AbstractClientTransport {

    private Logger logger = LoggerFactory.getLogger(AbstractTcpClientTransport.class);


    /**
     *
     */
    private final AtomicInteger msgId = new AtomicInteger();


    private final ConcurrentHashMap<Integer,MsgFuture> msgFutureMap = new ConcurrentHashMap<Integer, MsgFuture>();


    public AbstractTcpClientTransport(String ip,int port,ClientTransportConfig clientTransportConfig) {
        super(ip,port,clientTransportConfig);
    }

    @Override
    public ResponseMessage sendSync(RequestMessage msg, int timeout) {
        Integer msgId = null;
        try {
            msgId = generateMsgId();
            msg.setRequestMsgId(msgId);
            currentRequests.incrementAndGet();
            MsgFuture<ResponseMessage> msgFuture = doSendAsync(msg,timeout);
            return msgFuture.get(timeout, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e){
            throw new RpcException("request thread interrupted",e,msg);
        } catch (ClientTimeoutException e) {
            msgFutureMap.remove(msgId);
            throw e;
        } finally {
            currentRequests.decrementAndGet();
        }
    }

    @Override
    public MsgFuture sendAsync(RequestMessage msg, int timeout) {
        Integer msgId = null;
        try {
            msgId = generateMsgId();
            msg.setRequestMsgId(msgId);
            currentRequests.incrementAndGet();
            MsgFuture<ResponseMessage> msgFuture = doSendAsync(msg,timeout);
            return msgFuture;
        } catch (ClientTimeoutException e) {
            msgFutureMap.remove(msgId);
            throw e;
        } finally {
            currentRequests.decrementAndGet();
        }
    }


    /**
     * handle the response message
     *
     * @param responseMessage
     */
    public void handleResponse(ResponseMessage responseMessage){
        Integer msgId = responseMessage.getMsgId();
        MsgFuture future = msgFutureMap.get(msgId);
        if ( future == null ){
            logger.warn("MsgId[{}] MsgFuture no exist when handle the response message.Maybe has been removed for timeout",msgId);
            //the future is null,but important ,release msg body buf
            if (responseMessage != null && responseMessage.getMsgBodyBuf() != null){
                responseMessage.getMsgBodyBuf().release();
            }
            return;
        }
        future.setSuccess(responseMessage);
        msgFutureMap.remove(msgId);
    }

    abstract MsgFuture doSendAsync(RequestMessage msg,int timeout);

    private Integer generateMsgId(){

        return msgId.getAndIncrement();
    }

    protected void addMsgFuture(AbstractMessage msg,MsgFuture msgFuture){
        int msgType = msg.getHead().getMessageType();
        int msgId = msg.getMsgId();
        if ( msgType == REQUEST_MSG ||
             msgType == HEARTBEAT_REQUEST_MSG ||
             msgType == CALLBACK_REQUEST_MSG){
           msgFutureMap.put(msgId,msgFuture);
        } else {
            logger.error("can't handle this type message:{}",msg);
        }
    }
}
