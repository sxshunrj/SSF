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
 * @since 2016/03/21 18:13
 */
public class ClientTimeoutException extends RpcException {


    public ClientTimeoutException(AbstractMessage msg) {
        super(msg);
    }

    public ClientTimeoutException(String message, AbstractMessage msg) {
        super(message, msg);
    }

    public ClientTimeoutException(String message, Throwable cause, AbstractMessage msg) {
        super(message, cause, msg);
    }

    public ClientTimeoutException(Throwable cause, AbstractMessage msg) {
        super(cause, msg);
    }
}
