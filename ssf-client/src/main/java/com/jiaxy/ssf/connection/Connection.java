package com.jiaxy.ssf.connection;

import com.jiaxy.ssf.registry.Provider;
import com.jiaxy.ssf.transport.client.ClientTransport;
import com.jiaxy.ssf.transport.client.ClientTransportListener;
import com.jiaxy.ssf.util.NetUtil;

import java.util.Observable;

import static com.jiaxy.ssf.transport.client.ClientTransportFactory.ClientTransportKey;
import static com.jiaxy.ssf.transport.client.ClientTransportFactory.releaseClientTransport;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 09:51
 */
public class Connection extends Observable {

    private Provider provider;

    private TransportPair transportPair;

    private ConnectionState state;


    public Connection(Provider provider) {
        this.provider = provider;
    }

    public boolean isConnected(){
        if ( transportPair != null && transportPair.transport != null && transportPair.transport.isConnected()){
            return true;
        }
        return false;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public ClientTransport getTransport() {
        if (transportPair != null){
            return transportPair.transport;
        } else {
            return null;
        }
    }

    public void setTransport(ClientTransportKey transportKey,final ClientTransport transport) {
        transportPair = new TransportPair(transportKey,transport);
        if (transport == null){
            return;
        }
        transport.addChangeListener(this,new ClientTransportListener() {
            @Override
            public void change() {
                if (transport.isConnected()){
                    changeState(ConnectionState.ALIVE);
                } else {
                    changeState(ConnectionState.DEAD);
                }
            }
        });
    }

    public ConnectionState getState() {
        return state;
    }

    public void setState(ConnectionState state) {
        this.state = state;
    }

    public void changeState(ConnectionState state){
        ConnectionState oldState = this.state;
        this.state = state;
        setChanged();
        notifyObservers(oldState);
    }

    public void close(){
        if (transportPair != null){
            releaseClientTransport(transportPair.key, transportPair.transport);
            transportPair.transport.removeChangeListener(this);
            transportPair.transport = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (provider != null ? !provider.equals(that.provider) : that.provider != null) return false;
        if (transportPair != null ? !transportPair.equals(that.transportPair) : that.transportPair != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = provider != null ? provider.hashCode() : 0;
        result = 31 * result + (transportPair != null ? transportPair.hashCode() : 0);
        return result;
    }

    private class TransportPair{

        private ClientTransportKey key;

        private ClientTransport transport;

        public TransportPair(ClientTransportKey key, ClientTransport transport) {
            this.key = key;
            this.transport = transport;
        }

    }

    @Override
    public String toString() {
        return transportPair != null && transportPair.transport != null ?
                NetUtil.channelToString(transportPair.transport.getLocalAddress(),transportPair.transport.getRemoteAddress()) :
                provider.toString();
    }
}
