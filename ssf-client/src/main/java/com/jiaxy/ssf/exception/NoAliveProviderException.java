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
 * @since 2016/04/18 11:58
 */
public class NoAliveProviderException extends RpcException {

    public NoAliveProviderException() {
    }

    public NoAliveProviderException(String message) {
        super(message);
    }

    public NoAliveProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAliveProviderException(Throwable cause) {
        super(cause);
    }

    public NoAliveProviderException(AbstractMessage msg) {
        super(msg);
    }
}
