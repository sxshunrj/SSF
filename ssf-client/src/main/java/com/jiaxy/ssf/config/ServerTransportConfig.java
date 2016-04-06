package com.jiaxy.ssf.config;

import com.jiaxy.ssf.common.ProtocolType;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/17 13:54
 */
public class ServerTransportConfig {

    private int port = 31616;

    private String host = "localhost";

    /**
     * protocol type
     */
    private ProtocolType protocolType = ProtocolType.SSF;

    private int BACKLOG = 35536;

    private int connectionTimeout = 5000;

    /**
     * thread pool max size
     */
    private int bizPoolSize = 200;

    /**
     * boss thread num
     */
    private int bossNioEventThreads = 0;


    /**
     * io thread num
     */
    private int workerNioEventThreads = 0;


    /**
     * payload 8M
     */
    private int payload = 8 * 1024 * 1024;

    /**
     * epoll or not
     */
    private boolean epoll = false;

    private boolean keepAlive = true;

    private boolean tcpNoDelay = true;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getBACKLOG() {
        return BACKLOG;
    }

    public void setBACKLOG(int BACKLOG) {
        this.BACKLOG = BACKLOG;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getBizPoolSize() {
        return bizPoolSize;
    }

    public void setBizPoolSize(int bizPoolSize) {
        this.bizPoolSize = bizPoolSize;
    }

    public int getBossNioEventThreads() {
        return bossNioEventThreads;
    }

    public void setBossNioEventThreads(int bossNioEventThreads) {
        this.bossNioEventThreads = bossNioEventThreads;
    }

    public int getWorkerNioEventThreads() {
        return workerNioEventThreads;
    }

    public void setWorkerNioEventThreads(int workerNioEventThreads) {
        this.workerNioEventThreads = workerNioEventThreads;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public boolean isEpoll() {
        return epoll;
    }

    public void setEpoll(boolean epoll) {
        this.epoll = epoll;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }
}
