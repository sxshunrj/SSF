package com.jiaxy.ssf.message;

import com.jiaxy.ssf.codec.protocol.Protocol;
import com.jiaxy.ssf.codec.protocol.ProtocolFactory;
import com.jiaxy.ssf.exception.ClientTimeoutException;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.util.NetUtil;
import io.netty.channel.Channel;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 17:35
 */
public class MsgFuture<V> implements Future<V>{

    /**
     * the future created time
     */
    private final long createTime = System.currentTimeMillis();

    private Channel channel;

    private volatile long sendTime;

    private int timeout;

    private final AbstractMessage msg;

    private boolean async;

    public MsgFuture(Channel channel, int timeout, AbstractMessage msg) {
        this.channel = channel;
        this.timeout = timeout;
        this.msg = msg;
    }

    /**
     * the result
     */
    private volatile V result;


    private volatile Throwable error;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (isDone()){
            return false;
        }
        synchronized (this){
            if (isDone()){
                return false;
            }
            error = new CancellationException();
            notifyAll();

        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return isDone0();
    }

    @Override
    public V get() throws InterruptedException {
        return get(timeout,TimeUnit.MILLISECONDS);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException {
        timeout = unit.toMillis(timeout);
        long remainingTime = timeout - ( sendTime - createTime );
        if ( remainingTime <= 0 ){
            if ( isDone() ){
                return getNow();
            }
        } else  {
            if ( await(remainingTime,TimeUnit.MILLISECONDS) ){
                return getNow();
            }
        }
        throw createClientTimeoutException(false);
    }


    /**
     *
     * @param result
     */
    public void setSuccess(V result){
        if (setSuccess0(result)){
            //TODO notify listener
            return;
        }
        throw new IllegalStateException("future completed:"+this);
    }

    public boolean isSuccess(){
        if ( result == null || error != null){
            return false;
        }
        return true;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isAsync() {
        return async;
    }

    private ClientTimeoutException createClientTimeoutException(boolean isCheckThread){
        long current = System.currentTimeMillis();
        StringBuilder msgSB = new StringBuilder();
        if ( sendTime > 0 ){
            msgSB.append("wait provider response timeout.");
            msgSB.append("client elapsed:");
            msgSB.append(sendTime - createTime);
            msgSB.append(" ms");
            msgSB.append(". server elapsed:");
            msgSB.append(current - sendTime);
            msgSB.append(" ms,consumer timeout config:");
            msgSB.append(timeout);
            msgSB.append(" ms.");
            msgSB.append("channel:");
            msgSB.append(NetUtil.channelToString(channel.localAddress(), channel.remoteAddress()));
        } else {
            msgSB.append("consumer send request timeout .");
            msgSB.append("request time:");
            msgSB.append(createTime);
            msgSB.append(",");
            msgSB.append("end time:");
            msgSB.append(current);
            msgSB.append("client elapsed:");
            msgSB.append(current - createTime);
            msgSB.append(" ms");
        }
        if ( isCheckThread ){
            msgSB.append(",throws by check thread");
        }
        return new ClientTimeoutException(msgSB.toString(),this.msg);
    }


    private boolean await(long remainingTime,TimeUnit unit) throws InterruptedException {
        return await0(unit.toNanos(remainingTime),true);
    }

    private boolean await0(long timeoutNanos,boolean interrupt) throws InterruptedException {
        if ( isDone() ){
           return true;
        }
        if ( timeoutNanos <= 0 ){
            return isDone();
        }
        if (interrupt && Thread.interrupted()){
            throw new InterruptedException(toString());
        }
        long startTime = System.nanoTime();
        long waitTime = timeoutNanos;
        boolean interrupted = false;
        try {
            synchronized (this){
                if (isDone()){
                    return true;
                }
                try {
                    for (;;){
                        try {
                            wait(waitTime / 1000000, (int)(waitTime % 1000000));
                        } catch (InterruptedException e){
                            interrupted = true;
                        }
                        if (isDone()){
                            return true;
                        } else {
                            waitTime = timeoutNanos - ( System.nanoTime() - startTime);
                            if ( waitTime <= 0){
                                return isDone();
                            }
                        }
                    }
                } finally {

                }
            }

        } finally {
           if ( interrupted ){
               Thread.currentThread().interrupt();
           }
        }
    }


    private boolean isDone0(){
        return result != null;
    }

    private boolean setSuccess0(V result){
        if (isDone()){
            return false;
        }
        synchronized (this){
            if (isDone()){
                return false;
            }
            this.result = result;
            notifyAll();
        }
        return true;
    }

    /**
     *
     * @return the result
     */
    private V getNow(){
        if ( result == null ){
            return null;
        }
        if ( error != null ){
            RpcException rpcException;
            if ( error instanceof RpcException ){
                rpcException = (RpcException) error;
                rpcException.setMsg(msg);
            } else {
                rpcException = new RpcException(error,msg);
            }
            throw rpcException;
        }
        if ( result instanceof ResponseMessage ){
            ResponseMessage resMsg = (ResponseMessage) result;
            if (resMsg.getMsgBodyBuf() != null){

                try {
                    Protocol protocol = ProtocolFactory.getProtocol(resMsg.getProtocolType(),
                            resMsg.getCodecType());
                    ResponseMessage realRes = protocol.decode(resMsg.getMsgBodyBuf(), ResponseMessage.class);
                    if (realRes.getResponse() != null){
                        resMsg.setResponse(realRes.getResponse());
                    } else if (realRes.getException() != null){
                        resMsg.setException(realRes.getException());
                    }
                } finally {
                    resMsg.getMsgBodyBuf().release();
                    resMsg.setMsgBodyBuf(null);
                }
            }
        }
        return result;

    }
}
