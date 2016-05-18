package com.jiaxy.ssf.config;

import com.jiaxy.ssf.common.Constants;
import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.common.StringUtil;
import com.jiaxy.ssf.server.Server;
import com.jiaxy.ssf.server.ServerManager;
import com.jiaxy.ssf.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     service provider container
 *
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/12 11:05
 */
public class ServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);

    private Server server;

    protected String protocol = "ssf";

    protected String host;

    protected int port = Constants.DEFAULT_SERVER_PORT;

    protected int bizThreads = 200;

    protected int workerIoThreads;

    protected int payload = Constants.PAYLOAD;

    protected boolean epoll = false;

    private String boundHost;

    private List<ProviderConfig> providerConfigs = new ArrayList<ProviderConfig>();


    public void start(){
        if (StringUtil.isEmpty(host)){
            host = NetUtil.tryActualHostByRegistryAddress(null);
            //windows(0.0.0.0) the same port can be bound by more process
            boundHost = Constants.WINDOWS ? host : NetUtil.ALL_ADDRESS;
        } else {
            boundHost = host;
        }
        int actualBoundPort = NetUtil.tryAvailablePort(boundHost,getPort());
        if ( actualBoundPort != getPort()){
            logger.info("the actual bound port is:{}.not the config port:{} for this port is disabled",
                    actualBoundPort,
                    getPort()
            );
            setPort(actualBoundPort);
        }
        for (ProviderConfig providerConfig : providerConfigs){
            providerConfig.doExport();
        }
        Server server = ServerManager.getServer(this);
        server.start();
    }


    /**
     * export all bound providers
     */
    private void exportProviders(){

    }


    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }


    public void addProvider(ProviderConfig providerConfig){
        if (!providerConfigs.contains(providerConfig)){
            providerConfigs.add(providerConfig);
        }
    }

    public void removeProvider(ProviderConfig providerConfig){
        providerConfigs.remove(providerConfig);
    }


    public ServerTransportConfig convert2ServerTransportConfig(){
        ServerTransportConfig transportConfig = new ServerTransportConfig();
        transportConfig.setPort(getPort());
        transportConfig.setProtocolType(ProtocolType.valueOf(getProtocol().toUpperCase()));
        transportConfig.setHost(getBoundHost());
        transportConfig.setBizPoolSize(getBizThreads());
        transportConfig.setWorkerNioEventThreads(getWorkerIoThreads());
        transportConfig.setPayload(getPayload());
        transportConfig.setEpoll(isEpoll());
        return transportConfig;
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBizThreads() {
        return bizThreads;
    }

    public void setBizThreads(int bizThreads) {
        this.bizThreads = bizThreads;
    }

    public int getWorkerIoThreads() {
        return workerIoThreads;
    }

    public void setWorkerIoThreads(int workerIoThreads) {
        this.workerIoThreads = workerIoThreads;
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

    public List<ProviderConfig> getProviderConfigs() {
        return providerConfigs;
    }

    public void setProviderConfigs(List<ProviderConfig> providerConfigs) {
        this.providerConfigs = providerConfigs;
    }

    public String getBoundHost() {
        return boundHost;
    }
}
