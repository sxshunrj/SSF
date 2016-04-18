package com.jiaxy.ssf.client;

import com.jiaxy.ssf.client.balance.*;
import com.jiaxy.ssf.config.ConsumerConfig;
import com.jiaxy.ssf.connection.Connection;
import com.jiaxy.ssf.connection.ConnectionManager;
import com.jiaxy.ssf.exception.ConnectionClosedException;
import com.jiaxy.ssf.exception.NoAliveProviderException;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.registry.Provider;

import java.util.Iterator;
import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 09:41
 */
public abstract class AbstractClient implements Client {


    protected LoadBalance loadBalance;

    protected ConsumerConfig consumerConfig;

    private ConnectionManager connectionManager;


    public AbstractClient(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
        loadBalance = buildLoadBalance();
        initConnectionManager();
    }

    @Override
    public ResponseMessage sendMsg(RequestMessage requestMessage) {
        try {
            return doSendMsg(requestMessage);
        } finally {

        }
    }

    protected abstract ResponseMessage doSendMsg(RequestMessage requestMessage);


    protected ResponseMessage sendMsg0(RequestMessage requestMessage,Connection connection){
        String method = requestMessage.getMethodName();
        boolean isAsync = consumerConfig.isMethodAsync(method);
        int timeout = consumerConfig.methodTimeout(method);
        ResponseMessage responseMessage = null;
        try {
            if (isAsync) {
                //TODO async

            } else {//sync call
                responseMessage = connection.getTransport().sendSync(requestMessage, timeout);
            }
        } catch (ConnectionClosedException e) {
            //TODO handle
        }
        return responseMessage;
    }

    /**
     *
     * @param requestMessage request
     *
     * @param invokedProviders has invoked
     *
     * @return connection
     */
    protected Connection selectConnection(RequestMessage requestMessage,List<Provider> invokedProviders){
        //TODO sticky
        List<Provider> aliveProviders = connectionManager.getAliveProviders();
        aliveProviders.removeAll(invokedProviders);
        if ( aliveProviders.size() == 0 ){
            throw new NoAliveProviderException(String.format(
                    "%s no alive provider.current providers is:",
                    consumerConfig.buildUniqueKey(),
                    connectionManager.getAllProviders()));
        }
        Connection connection = null;
        Iterator<Provider> it = aliveProviders.iterator();
        while (it.hasNext()){
            Provider provider = loadBalance.select(requestMessage, aliveProviders);
            connection = connectionManager.getAliveConnection(provider);
            it.remove();
            if (connection != null && connection.isConnected()){
                break;
            }
        }
        if ( connection == null ){
             throw new NoAliveProviderException(String.format(
                    "%s no alive provider.current providers is:",
                    consumerConfig.buildUniqueKey(),
                    connectionManager.getAllProviders()));
        }
        return connection;
    }


    protected List<Provider> buildProviderList(){
        //1、direct url
        //2、get provider list from registry
        return null;
    }

    protected void initConnectionManager(){
        connectionManager = new ConnectionManager();
        List<Provider> providers = buildProviderList();
    }

    protected LoadBalance buildLoadBalance(){
        switch (consumerConfig.getLBStrategy()){
            case RANDOM:
                return new RandomLoadBalance();
            case ROUNDROBIN:
                return new RoundRobinLoadBalance();
            case LEASTACTIVE:
                return new LeastActiveLoadBalance();
            case LOCALPREFERENCE:
                return new LocalPreferenceLoadBalance();
            case CONSISTENTHASH:
                return new ConsistentHashLoadBalance();
            default:
                return new RandomLoadBalance();
        }
    }

}
