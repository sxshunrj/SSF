package com.jiaxy.ssf.transport;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.config.ClientTransportConfig;

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
    private final static ConcurrentHashMap<String,ClientTransportContainer> connectionPool = new ConcurrentHashMap<String, ClientTransportContainer>();

    /**
     * counter of one connection is using
     */
    private final static ConcurrentHashMap<ClientTransport,AtomicInteger> connectionRefCount = new ConcurrentHashMap<ClientTransport,AtomicInteger>();




    public static ClientTransport getClientTransport(ProtocolType protocolType,String ip,int port,ClientTransportConfig clientTransportConfig){
        String key = createKey(protocolType,ip,port);
        ClientTransportContainer ctc = connectionPool.get(key);
        if ( ctc == null ){
            ctc = new ClientTransportContainer();
            ClientTransportContainer oldCtc = connectionPool.putIfAbsent(key,ctc);
            if ( oldCtc != null ){
                ctc = oldCtc;
            }
        }
        if ( ctc.isInitialized() ){
            synchronized ( ctc ){
                if ( ctc.isInitialized() ){
                    ClientTransport clientTransport = createClientTransport(protocolType,clientTransportConfig);
                    ctc.setClientTransport(clientTransport);
                    //connect the server
                    clientTransport.connect();
                    connectionRefCount.putIfAbsent(clientTransport,new AtomicInteger(0));
                }
            }
        }
        return ctc.getClientTransport();
    }


    public static void releaseClientTransport(ClientTransport clientTransport,int timeout){


    }

    private static ClientTransport createClientTransport(ProtocolType protocolType,ClientTransportConfig clientTransportConfig){
        ClientTransport clientTransport = null;
        switch ( protocolType ){
            case SSF:
                clientTransport = new SSFClientTransport(clientTransportConfig);
                break;
            default:
                clientTransport = new SSFClientTransport(clientTransportConfig);
                break;
        }
        return clientTransport;
    }


    private static String createKey(ProtocolType protocolType,String ip,int port){
        return protocolType.name() +"::"+ip+"::"+port;
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
