package com.jiaxy.ssf.connection;

import com.jiaxy.ssf.registry.Provider;
import com.jiaxy.ssf.transport.client.ClientTransport;
import com.jiaxy.ssf.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
public class ConnectionManager implements Observer {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private ConcurrentHashMap<Provider,Connection> aliveConnections = new ConcurrentHashMap<Provider, Connection>();

    private ConcurrentHashMap<Provider,Connection> retryConnections = new ConcurrentHashMap<Provider, Connection>();

    private ConcurrentHashMap<Provider,Connection> deadConnections = new ConcurrentHashMap<Provider, Connection>();


    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void update(Observable observable, Object oldState) {
        Lock writeLock = lock.writeLock();
        try {
            ConnectionState oldConnState = (ConnectionState) oldState;
            Connection connection = (Connection) observable;
            switch (oldConnState){
                case ALIVE:
                    removeAliveConnection(connection.getProvider());
                    break;
                case RETRY:
                    removeRetryConnection(connection.getProvider());
                    break;
                case DEAD:
                    removeDeadConnection(connection.getProvider());
                    break;
            }
            addConnection(connection);
        } finally {
            writeLock.unlock();
        }

    }


    public void addConnection(Connection connection){
        switch (connection.getState()){
            case ALIVE:
                addAliveConnection(connection.getProvider(),connection);
                break;
            case RETRY:
                addRetryConnection(connection.getProvider(),connection);
                break;
            case DEAD:
                addDeadConnection(connection.getProvider(),connection);
                break;
        }
    }

    public Connection getAliveConnection(Provider provider){
        return aliveConnections.get(provider);
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


    /**
     * double check if the connection is connected
     *
     * @param interfaceName
     *
     * @param connection
     *
     * @return
     */
    public boolean doubleCheck(String interfaceName,Connection connection){
        if (connection.isConnected()){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
            }
            if (connection.isConnected()){
                return true;
            } else {
                logger.warn("connection has been closed.Maybe reached the connection threshold of [{}] or this ip {} in the blacklist of the [{}] provider",
                        new Object[]{
                            NetUtil.ipPortString(connection.getTransport().getRemoteAddress()),
                            NetUtil.ipString(connection.getTransport().getLocalAddress()),
                            interfaceName
                        }
                );
                return false;
            }
        }
        return false;
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

    private void addAliveConnection(Provider provider,Connection connection){
        aliveConnections.put(provider,connection);
    }

    private void addRetryConnection(Provider provider,Connection connection){
        retryConnections.put(provider,connection);
    }

    private void addDeadConnection(Provider provider,Connection connection){
        deadConnections.put(provider,connection);
    }

    private void removeAliveConnection(Provider provider){
        aliveConnections.remove(provider);
    }

    private void removeRetryConnection(Provider provider){
        retryConnections.remove(provider);
    }

    private void removeDeadConnection(Provider provider){
        deadConnections.remove(provider);
    }


}
