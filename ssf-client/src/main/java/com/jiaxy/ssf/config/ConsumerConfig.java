package com.jiaxy.ssf.config;

import com.jiaxy.ssf.client.Client;
import com.jiaxy.ssf.client.balance.LoadBalanceStrategy;
import com.jiaxy.ssf.client.cluster.ClusterStrategy;
import com.jiaxy.ssf.client.cluster.FailfastClient;
import com.jiaxy.ssf.client.cluster.FailoverClient;
import com.jiaxy.ssf.common.Constants;
import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.exception.InitException;
import com.jiaxy.ssf.intercept.MessageInvocationFactory;
import com.jiaxy.ssf.processor.ConsumerProcessor;
import com.jiaxy.ssf.processor.MessageProcessor;
import com.jiaxy.ssf.proxy.ProxyType;
import com.jiaxy.ssf.proxy.ServiceProxyFactory;
import com.jiaxy.ssf.service.GenericService;
import com.jiaxy.ssf.util.Callbacks;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/11 18:34
 */
public class ConsumerConfig<T> extends SSFConfig{

    public static final String ASYNC = "async";

    public static final String TIMEOUT = "timeout";

    //the number of retries
    public static final String RETRIES = "retries";

    /**
     * 5s
     */
    private int timeout = 5000;

    private ProtocolType protocol;

    /**
     * direct url
     */
    private String url;

    private boolean generic = false;

    private boolean async = false;

    private int connectionTimeout = Constants.DEFAULT_CLIENT_CONNECT_TIMEOUT;

    private ClusterStrategy strategy = ClusterStrategy.FAILOVER;

    private LoadBalanceStrategy lbStrategy = LoadBalanceStrategy.RANDOM;

    private boolean lazy = false;

    private boolean sticky = false;

    private boolean inJVM = true;

    private boolean subscribe = true;

    private boolean epoll = false;

    private int payload = 8 * 1024 * 1024;


    /**
     * default retry 0 times
     */
    private int retries = 0;



    private T proxy;

    private MessageProcessor processor;

    private Client client;

    public T refer() throws InitException{
        if ( proxy != null ){
            return proxy;
        }
        synchronized (this){
            try {
                buildClient();
                //cache callback method info
                Callbacks.callbackInfoRegister(getProxyClass());
                processor = new ConsumerProcessor(MessageInvocationFactory.getMessageInvocation(this),this);
                proxy = (T) ServiceProxyFactory.getProxy(ProxyType.JDK,getProxyClass(),processor);
                return proxy;
            }catch (Throwable e){
                if ( e instanceof InitException){
                    throw (InitException)e;
                } else {
                    throw new InitException("build consumer error",e);
                }
            }
        }
    }


    public void unRefer(){
        if (client != null){
            client.close();
        }
    }



    @Override
    public String buildUniqueKey() {
        return "consumer://"+serviceInterfaceName+":"+alias;
    }

    @Override
    public Class<?> getProxyClass() {
        if (generic){
            return GenericService.class;
        }
        return getServiceInterfaceClass();
    }


    private void buildClient(){
        switch (this.getStrategy()){
            case FAILOVER:
                client = new FailoverClient(this);
                break;
            case FAILFAST:
                client = new FailfastClient(this);
                break;
            default:
                client = new FailoverClient(this);
                break;
        }
    }

    /**
     *
     * @param method
     * @return the number of retries
     */
    public int getRetriesNumber(String method){
        return (Integer)getMethodConfigValue(method,RETRIES,getRetries());
    }

    public boolean isMethodAsync(String method){
        return (Boolean)getMethodConfigValue(method,ASYNC,isAsync());
    }

    public int methodTimeout(String method){
        return (Integer)getMethodConfigValue(method,TIMEOUT,getTimeout());
    }

    public ProtocolType getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isGeneric() {
        return generic;
    }

    public void setGeneric(boolean generic) {
        this.generic = generic;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public ClusterStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ClusterStrategy strategy) {
        this.strategy = strategy;
    }

    public LoadBalanceStrategy getLBStrategy() {
        return lbStrategy;
    }

    public void setLBStrategy(LoadBalanceStrategy lbStrategy) {
        this.lbStrategy = lbStrategy;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public boolean isInJVM() {
        return inJVM;
    }

    public void setInJVM(boolean inJVM) {
        this.inJVM = inJVM;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public T getProxy() {
        return proxy;
    }

    public void setProxy(T proxy) {
        this.proxy = proxy;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
