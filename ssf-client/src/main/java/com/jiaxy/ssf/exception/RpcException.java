package com.jiaxy.ssf.exception;

import com.jiaxy.ssf.message.AbstractMessage;

import java.io.Serializable;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 18:15
 */
public class RpcException extends RuntimeException implements Serializable {

    /**
     * bind up rpc exception with this message
     */
    private transient AbstractMessage msg;

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(AbstractMessage msg) {
        this.msg = msg;
    }

    public RpcException(String message, AbstractMessage msg) {
        super(message);
        this.msg = msg;
    }

    public RpcException(String message, Throwable cause, AbstractMessage msg) {
        super(message, cause);
        this.msg = msg;
    }

    public RpcException(Throwable cause, AbstractMessage msg) {
        super(cause);
        this.msg = msg;
    }

    public AbstractMessage getMsg() {
        return msg;
    }

    public void setMsg(AbstractMessage msg) {
        this.msg = msg;
    }

    public static RpcException convertToRpcException(AbstractMessage msg,Throwable e){
        RpcException rpcException;
        if ( e instanceof RpcException ){
            rpcException = (RpcException) e;
            rpcException.setMsg(msg);
        } else {
            return new RpcException(e,msg);
        }
        return rpcException;
    }
}
