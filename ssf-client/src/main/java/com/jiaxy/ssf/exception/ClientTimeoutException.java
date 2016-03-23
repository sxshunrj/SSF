package com.jiaxy.ssf.exception;

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

    public ClientTimeoutException() {
    }

    public ClientTimeoutException(String message) {
        super(message);
    }

    public ClientTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientTimeoutException(Throwable cause) {
        super(cause);
    }
}
