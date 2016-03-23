package com.jiaxy.ssf.transport;

import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.exception.ClientTimeoutException;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.MsgFuture;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
public abstract class AbstractTcpClientTransport extends AbstractClientTransport{

    private Logger logger = LoggerFactory.getLogger(AbstractTcpClientTransport.class);


    /**
     *
     */
    private final AtomicInteger msgId = new AtomicInteger();


    private final ConcurrentHashMap<Integer,MsgFuture> msgFutureMap = new ConcurrentHashMap<Integer, MsgFuture>();


    public AbstractTcpClientTransport(ClientTransportConfig clientTransportConfig) {
        super(clientTransportConfig);
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
            throw new RpcException("request thread interrupted",e);
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

    abstract MsgFuture doSendAsync(RequestMessage msg,int timeout);

    private Integer generateMsgId(){
        return msgId.getAndIncrement();
    }
}
