package com.jiaxy.ssf.provider;

import com.jiaxy.ssf.config.ServerConfig;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *    create {@link com.jiaxy.ssf.provider.Server}
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/14 15:24
 */
public class ServerManager {


    /**
     * key:server port
     * value:server
     */
    private static final ConcurrentHashMap<Integer,Server> servers = new ConcurrentHashMap<Integer, Server>();


    public static Server getServer(ServerConfig config){
        Server server = servers.get(config.getPort());
        if ( server == null ){
            synchronized (ServerManager.class){
                server = buildServer(config);
                return server;
            }
        }
        return server;
    }


    private static Server buildServer(ServerConfig config){
        Server server = new Server(config);
        servers.put(config.getPort(),server);
        return server;
    }

}
