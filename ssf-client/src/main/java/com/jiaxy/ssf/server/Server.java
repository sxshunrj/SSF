package com.jiaxy.ssf.server;

import com.jiaxy.ssf.config.ServerConfig;
import com.jiaxy.ssf.config.ServerTransportConfig;
import com.jiaxy.ssf.transport.server.SSFServerTransport;
import com.jiaxy.ssf.transport.server.ServerTransport;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/06 17:11
 */
public class Server {


    private ServerConfig serverConfig;

    private ServerTransport serverTransport;

    private volatile boolean started;

    public Server(ServerConfig config) {
        this.serverConfig = config;
        this.serverTransport = buildServerTransport();
    }

    public void start(){
        if (started){
            return;
        }
        synchronized (this){
            serverTransport.start();
            started = true;
        }
    }

    public void stop(){
        if (started){
            serverTransport.stop();
        }
    }


    private ServerTransport buildServerTransport(){
        ServerTransportConfig transportConfig = serverConfig.convert2ServerTransportConfig();
        switch ( transportConfig.getProtocolType() ){
            case SSF:
                return new SSFServerTransport(transportConfig);
            default:
                return new SSFServerTransport(transportConfig);
        }
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
