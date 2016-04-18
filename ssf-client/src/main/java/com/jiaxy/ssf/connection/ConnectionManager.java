package com.jiaxy.ssf.connection;

import com.jiaxy.ssf.registry.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     one client connection manager
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 09:51
 */
public class ConnectionManager {

    private ConcurrentHashMap<Provider,Connection> aliveConnections = new ConcurrentHashMap<Provider, Connection>();

    private ConcurrentHashMap<Provider,Connection> retryConnections = new ConcurrentHashMap<Provider, Connection>();

    private ConcurrentHashMap<Provider,Connection> deadConnections = new ConcurrentHashMap<Provider, Connection>();


    private ReadWriteLock lock = new ReentrantReadWriteLock();


    public void addConnection(Provider provider,Connection connection){

    }


    public Connection getAliveConnection(Provider provider){
        //TODO
        return null;
    }


    public List<Provider> getAliveProviders(){
        return new ArrayList<Provider>(aliveConnections.keySet());
    }


    public List<Provider> getAllProviders(){
        List<Provider> all = new ArrayList<Provider>();
        Lock readLock = lock.readLock();
        try {
            all.addAll(aliveConnections.keySet());
            all.addAll(retryConnections.keySet());
            all.addAll(deadConnections.keySet());
            return all;
        } finally {
            readLock.unlock();
        }
    }


    public ConcurrentHashMap<Provider, Connection> getAliveConnections() {
        return aliveConnections;
    }

    public ConcurrentHashMap<Provider, Connection> getRetryConnections() {
        return retryConnections;
    }

    public ConcurrentHashMap<Provider, Connection> getDeadConnections() {
        return deadConnections;
    }
}
