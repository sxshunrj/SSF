package com.jiaxy.ssf.client;

import com.jiaxy.ssf.client.balance.*;
import com.jiaxy.ssf.common.StringUtil;
import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.config.ConsumerConfig;
import com.jiaxy.ssf.connection.Connection;
import com.jiaxy.ssf.connection.ConnectionManager;
import com.jiaxy.ssf.connection.ConnectionState;
import com.jiaxy.ssf.exception.ConnectionClosedException;
import com.jiaxy.ssf.exception.NoAliveProviderException;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.registry.Provider;
import com.jiaxy.ssf.thread.NamedThreadFactory;
import com.jiaxy.ssf.thread.dreamwork.AbstractDreamTask;
import com.jiaxy.ssf.thread.dreamwork.Dreamwork;
import com.jiaxy.ssf.transport.client.ClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static com.jiaxy.ssf.transport.client.ClientTransportFactory.buildKey;
import static com.jiaxy.ssf.transport.client.ClientTransportFactory.getClientTransport;
import static com.jiaxy.ssf.connection.ConnectionState.*;

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

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

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

    @Override
    public void close() {

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
        if (invokedProviders != null){
            aliveProviders.removeAll(invokedProviders);
        }
        if ( aliveProviders.size() == 0 ){
            throw new NoAliveProviderException(String.format(
                    "%s no alive provider.current providers is:%s",
                    consumerConfig.buildUniqueKey(),
                    connectionManager.getAllProviders()));
        }
        Connection connection = null;
        Iterator<Provider> it = aliveProviders.iterator();
        while (it.hasNext()){
            it.next();
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
        List<Provider> providers = new ArrayList<Provider>();
        String urls = consumerConfig.getUrl();
        //1、direct url
        if (!StringUtil.isEmpty(urls)){
            urls = urls.replaceAll(",",";");
            String[] urlArr = urls.split(";");
            for (String url : urlArr){
                providers.add(Provider.build(url));
            }
        } else {
            //2、get provider list from registry
        }
        return providers;
    }

    protected void initConnectionManager(){
        connectionManager = new ConnectionManager();
        List<Provider> providers = buildProviderList();
        buildConnections(providers);
    }

    /**
     * build the connections with providers
     *
     * @param providers
     */
    protected void buildConnections(List<Provider> providers){
        final String interfaceName = consumerConfig.getServiceInterfaceName();
        int threadNum = Math.min(10, providers.size());
        final CountDownLatch downLatch = new CountDownLatch(threadNum);
        int connectTimeout = consumerConfig.getConnectionTimeout();
        Dreamwork dreamwork = new Dreamwork(false,
                0,
                threadNum,
                0,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>(100),
                new NamedThreadFactory("SSF-CLI-CONN-"+interfaceName+"-",true)
                );
        for (final Provider provider : providers){
            dreamwork.execute(new AbstractDreamTask() {
                @Override
                public void run() {
                    Connection connection = new Connection(provider);
                    try {
                        ClientTransport clientTransport = getClientTransport(
                                buildKey(consumerConfig.getProtocol(), provider.getIp(), provider.getPort()),
                                ClientTransportConfig.build(consumerConfig)
                        );
                        connection.setTransport(clientTransport);
                        if (connectionManager.doubleCheck(interfaceName,connection)){
                            printSuccess(interfaceName,provider,connection);
                            connection.setState(ALIVE);
                        } else {
                            printFailure(interfaceName,provider);
                            connection.setState(RETRY);
                        }
                    } catch (Exception e){
                        printFailure(interfaceName,provider,e);
                        connection.setState(DEAD);
                    } finally {
                        downLatch.countDown();
                    }
                    connectionManager.addConnection(connection);
                }
            });
        }
        try {
            long totalWaitTime = (providers.size() % threadNum == 0 ?
                    providers.size() / threadNum :
                    providers.size() / threadNum + 1) * connectTimeout;
            downLatch.await(totalWaitTime,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("build connection error:{}",e.getMessage());
        } finally {
            dreamwork.shutdown();
        }
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


    private void printSuccess(String interfaceName,Provider provider,Connection connection){
        logger.info("Connected to {} provider{} success.the connection is:{}",
                new Object[]{interfaceName,provider,connection});
    }

    private void printFailure(String interfaceName,Provider provider){
        logger.info("Connected to {} provider{} failed.",
                new Object[]{interfaceName,provider});
    }

    private void printFailure(String interfaceName,Provider provider,Exception e){
        logger.info("Connected to {} provider{} failed.the exception is:{},message:{},cause by:{}",
                new Object[]{interfaceName,provider,e.getClass().getCanonicalName(),e.getMessage(),e.getCause()});
    }

}
