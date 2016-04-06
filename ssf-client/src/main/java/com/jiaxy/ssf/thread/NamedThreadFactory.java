package com.jiaxy.ssf.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/17 15:39
 */
public class NamedThreadFactory implements ThreadFactory{

    private final AtomicInteger count = new AtomicInteger();

    private final ThreadGroup group;

    private final String namePrefix;

    private boolean isDaemon;


    public NamedThreadFactory(String namePrefix, boolean isDaemon) {
        SecurityManager securityManager = System.getSecurityManager();
        group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
        this.isDaemon = isDaemon;
    }

    public NamedThreadFactory(String namePrefix) {
        this(namePrefix,false);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group,r,namePrefix + count.getAndIncrement(),0);
        thread.setDaemon(isDaemon);
        thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }

    public void setDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
    }
}
