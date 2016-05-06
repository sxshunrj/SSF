package com.jiaxy.ssf.thread.dreamwork;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2015/10/29 18:06
 */
public abstract class AbstractDreamTask implements DreamTask {

    @Override
    public String dream() {
        return "";
    }

    @Override
    public long executedTime() {
        return 0;
    }

    @Override
    public long createdTime() {
        return 0;
    }

    @Override
    public int timeout() {
        return 0;
    }

}
