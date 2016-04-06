package com.jiaxy.ssf.thread.dreamwork;

import java.util.concurrent.RejectedExecutionException;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2015/05/18 15:17
 */
public class DreamRejectException extends RejectedExecutionException{

    public DreamRejectException() {
    }

    public DreamRejectException(String message) {
        super(message);
    }

    public DreamRejectException(String message, Throwable cause) {
        super(message, cause);
    }

    public DreamRejectException(Throwable cause) {
        super(cause);
    }
}
