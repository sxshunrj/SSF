package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.util.NetUtil;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/23 13:59
 */
public class ClientTransportFactory {


    /**
     * one connection is shared by ip and port and protocol
     */
    private final static ConcurrentHashMap<ClientTransportKey,ClientTransportContainer> connectionPool = new ConcurrentHashMap<ClientTransportKey, ClientTransportContainer>();

    /**
     * counter of one connection is using
     */
    private final static ConcurrentHashMap<ClientTransport,AtomicInteger> connectionRefCount = new ConcurrentHashMap<ClientTransport,AtomicInteger>();


    public static ClientTransport getClientTransport(ClientTransportKey key,Channel channel){
        ClientTransportContainer ctc = connectionPool.get(key);
        if ( ctc == null ){
            ctc = new ClientTransportContainer();
            ClientTransportContainer oldCtc = connectionPool.putIfAbsent(key,ctc);
            if ( oldCtc != null ){
                ctc = oldCtc;
            }
        }
        if ( !ctc.isInitialized() ){
            synchronized ( ctc ){
                if ( !ctc.isInitialized() ){
                    ClientTransport clientTransport = createClientTransport(key,channel);
                    ctc.setClientTransport(clientTransport);
                    connectionRefCount.putIfAbsent(clientTransport,new AtomicInteger(0));
                }
            }
        }
        return ctc.getClientTransport();
    }


    public static ClientTransport getClientTransport(ClientTransportKey key,ClientTransportConfig clientTransportConfig){
        ClientTransportContainer ctc = connectionPool.get(key);
        if ( ctc == null ){
            ctc = new ClientTransportContainer();
            ClientTransportContainer oldCtc = connectionPool.putIfAbsent(key,ctc);
            if ( oldCtc != null ){
                ctc = oldCtc;
            }
        }
        if ( !ctc.isInitialized() ){
            synchronized ( ctc ){
                if ( !ctc.isInitialized() ){
                    ClientTransport clientTransport = createClientTransport(key,clientTransportConfig);
                    ctc.setClientTransport(clientTransport);
                    //connect the server
                    clientTransport.connect();
                    connectionRefCount.putIfAbsent(clientTransport,new AtomicInteger(0));
                }
            }
        }
        return ctc.getClientTransport();
    }


    public static void releaseClientTransport(ClientTransportKey key,ClientTransport clientTransport,int timeout){
        if ( clientTransport == null ){
            return;
        }
        AtomicInteger count = connectionRefCount.get(clientTransport);
        if ( count == null ){
            connectionPool.remove(key);
            clientTransport.disConnect();
            return;
        }
        if ( count.get() <= 0 ){
            connectionPool.remove(key);
            clientTransport.disConnect();
        }
    }

    public static void releaseClientTransportDirectly(ClientTransportKey key){
        ClientTransportContainer cc = connectionPool.remove(key);
        if (cc != null && cc.getClientTransport() != null){
            connectionPool.remove(cc.getClientTransport());
            connectionRefCount.remove(cc.getClientTransport());
        }
    }

    public static ClientTransportKey buildKey(ProtocolType protocolType, String ip, int port){
        return new ClientTransportKey(protocolType,ip,port);
    }

    private static ClientTransport createClientTransport(ClientTransportKey key,ClientTransportConfig clientTransportConfig){
        ClientTransport clientTransport = null;
        switch ( key.protocolType ){
            case SSF:
                clientTransport = new SSFClientTransport(key.ip,key.port,clientTransportConfig);
                break;
            default:
                clientTransport = new SSFClientTransport(key.ip,key.port,clientTransportConfig);
                break;
        }
        return clientTransport;
    }

    private static ClientTransport createClientTransport(ClientTransportKey key,Channel channel){
        ClientTransport clientTransport = null;
        switch ( key.protocolType ){
            case SSF:
                clientTransport = new SSFClientTransport(key.ip,key.port,channel);
                break;
            default:
                clientTransport = new SSFClientTransport(key.ip,key.port,channel);
                break;
        }
        return clientTransport;
    }

    public static class ClientTransportKey {

        private ProtocolType protocolType;

        private String ip;

        private int port;

        public ClientTransportKey(ProtocolType protocolType, String ip, int port) {
            this.protocolType = protocolType;
            this.ip = ip;
            this.port = port;
        }

        public ProtocolType getProtocolType() {
            return protocolType;
        }

        public void setProtocolType(ProtocolType protocolType) {
            this.protocolType = protocolType;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClientTransportKey that = (ClientTransportKey) o;

            if (port != that.port) return false;
            if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
            if (protocolType != that.protocolType) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = protocolType != null ? protocolType.hashCode() : 0;
            result = 31 * result + (ip != null ? ip.hashCode() : 0);
            result = 31 * result + port;
            return result;
        }
    }

    static class ClientTransportContainer{

        private ClientTransport clientTransport;

        public ClientTransportContainer() {
        }


        public boolean isInitialized(){
            if ( clientTransport != null && clientTransport.isConnected() ){
                return true;
            } else {
                return false;
            }
        }

        public ClientTransport getClientTransport() {
            return clientTransport;
        }

        public void setClientTransport(ClientTransport clientTransport) {
            this.clientTransport = clientTransport;
        }
    }


}
