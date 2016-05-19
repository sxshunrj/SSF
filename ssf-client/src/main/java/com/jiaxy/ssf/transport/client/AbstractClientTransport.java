package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.message.MessageBuilder;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.thread.NamedThreadFactory;
import com.jiaxy.ssf.transport.client.ClientTransport;
import com.jiaxy.ssf.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
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

    private static final Logger logger = LoggerFactory.getLogger(AbstractClientTransport.class);

    protected String remoteIP;

    protected int remotePort;

    protected InetSocketAddress localAddress;

    protected InetSocketAddress remoteAddress;

    protected ClientTransportConfig clientTransportConfig;

    protected AtomicInteger currentRequests = new AtomicInteger();

    protected CopyOnWriteArraySet<ClientTransportListener> listeners = new CopyOnWriteArraySet<ClientTransportListener>();

    protected final ScheduledExecutorService hbThread = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("SSF-HB-"+ NetUtil.ipPortString(remoteAddress)+"-"));

    protected final ScheduledExecutorService retryConnectThread = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("SSF-RETRY-"+ NetUtil.ipPortString(remoteAddress)+"-"));

    private volatile boolean hbThreadStart = false;

    private volatile boolean retryConnectThreadStart = false;

    private volatile boolean doHeartbeat = true;


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

    @Override
    public void addChangeListener(ClientTransportListener listener) {
        listeners.add(listener);
    }

    protected void startHeartbeatThread(){
        if (hbThreadStart){
            return;
        }
        hbThreadStart = true;
        logger.info("[{}] heartbeat thread started",NetUtil.ipPortString(remoteAddress));
        //TODO config the delay
        hbThread.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                sendHeartbeat();
            }
        },1000,5000, TimeUnit.MILLISECONDS);
    }

    protected void startRetryConnectThread(){
        if (retryConnectThreadStart){
            return;
        }
        retryConnectThreadStart = true;
        logger.info("[{}] retry connect thread started",NetUtil.ipPortString(remoteAddress));
        retryConnectThread.scheduleWithFixedDelay(new Runnable() {
           @Override
           public void run() {
               if (doHeartbeat){
                   return;
               }
               try {
                   connect();
               }catch (Throwable e){
               }
               if (isConnected()){
                   logger.info("{} connected by retry",NetUtil.channelToString(localAddress,remoteAddress));
                   doHeartbeat = true;
                   for (ClientTransportListener listener : listeners){
                       listener.change();
                   }
               }
           }
       },1000,5000,TimeUnit.MILLISECONDS);
    }

    private void sendHeartbeat(){
        RequestMessage heartbeat = MessageBuilder.buildHeartbeatRequest();
        //TODO config the timeout
        try {
            if (!doHeartbeat){
                return;
            }
            ResponseMessage responseMessage = sendSync(heartbeat,3000);
            if (responseMessage.isError()){
                logger.error("send heartbeat error in channel:{}",
                        NetUtil.channelToString(localAddress,remoteAddress),responseMessage.getException());
                doHeartbeat = false;
            }
        } catch (Throwable e){
            logger.error("send heartbeat error in channel:{}",
                    NetUtil.channelToString(localAddress,remoteAddress),e);
            doHeartbeat = false;
        }
        if (!doHeartbeat){
            for (ClientTransportListener listener : listeners){
                listener.change();
            }
        }
    }
}
