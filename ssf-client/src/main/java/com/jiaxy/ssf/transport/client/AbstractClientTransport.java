package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.transport.client.ClientTransport;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 17:39
 */
public abstract class AbstractClientTransport implements ClientTransport {

    protected String remoteIP;

    protected int remotePort;

    protected InetSocketAddress localAddress;

    protected InetSocketAddress remoteAddress;

    protected ClientTransportConfig clientTransportConfig;

    protected AtomicInteger currentRequests = new AtomicInteger();

    public AbstractClientTransport(String ip,int port,ClientTransportConfig clientTransportConfig) {
        this.remoteIP = ip;
        this.remotePort = port;
        this.clientTransportConfig = clientTransportConfig;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(InetSocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public ClientTransportConfig getClientTransportConfig() {
        return clientTransportConfig;
    }

    public void setClientTransportConfig(ClientTransportConfig clientTransportConfig) {
        this.clientTransportConfig = clientTransportConfig;
    }

    @Override
    public int currentRequests() {
        return currentRequests.get();
    }
}
