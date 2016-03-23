package com.jiaxy.ssf.message;

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

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public V get() throws InterruptedException {
        return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }
}
