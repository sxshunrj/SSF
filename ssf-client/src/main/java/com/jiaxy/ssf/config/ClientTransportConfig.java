package com.jiaxy.ssf.config;

import com.jiaxy.ssf.common.Constants;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @since 2016/03/16 18:26
 */
public class ClientTransportConfig {


    private int connectionTimeout = Constants.DEFAULT_CLIENT_CONNECT_TIMEOUT;

    private int childNioEventThreads = 0;

    private boolean epoll = false;

    private int payload = 8 * 1024 * 1024;


    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getChildNioEventThreads() {
        return childNioEventThreads;
    }

    public void setChildNioEventThreads(int childNioEventThreads) {
        this.childNioEventThreads = childNioEventThreads;
    }

    public boolean isEpoll() {
        return epoll;
    }

    public void setEpoll(boolean epoll) {
        this.epoll = epoll;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }
}
