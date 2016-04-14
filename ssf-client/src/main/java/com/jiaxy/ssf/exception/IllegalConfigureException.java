package com.jiaxy.ssf.exception;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/12 21:22
 */
public class IllegalConfigureException extends InitException {


    public IllegalConfigureException() {
    }

    public IllegalConfigureException(String message) {
        super(message);
    }

    public IllegalConfigureException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalConfigureException(Throwable cause) {
        super(cause);
    }
}
