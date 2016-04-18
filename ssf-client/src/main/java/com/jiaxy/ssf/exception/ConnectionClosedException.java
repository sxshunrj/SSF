package com.jiaxy.ssf.exception;

import com.jiaxy.ssf.message.AbstractMessage;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 11:27
 */
public class ConnectionClosedException extends RpcException {

    public ConnectionClosedException() {
    }

    public ConnectionClosedException(String message) {
        super(message);
    }

    public ConnectionClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionClosedException(Throwable cause) {
        super(cause);
    }

    public ConnectionClosedException(AbstractMessage msg) {
        super(msg);
    }
}
