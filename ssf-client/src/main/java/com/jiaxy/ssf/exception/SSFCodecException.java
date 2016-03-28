package com.jiaxy.ssf.exception;

import java.io.Serializable;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 16:24
 */
public class SSFCodecException extends RpcException implements Serializable{

    public SSFCodecException() {
    }

    public SSFCodecException(String message) {
        super(message);
    }

    public SSFCodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public SSFCodecException(Throwable cause) {
        super(cause);
    }
}
