package com.jiaxy.ssf.connection;

import com.jiaxy.ssf.registry.Provider;
import com.jiaxy.ssf.transport.client.ClientTransport;
import com.jiaxy.ssf.transport.client.ClientTransportListener;
import com.jiaxy.ssf.util.NetUtil;

import java.util.Observable;

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

    private ClientTransport transport;

    private ConnectionState state;


    public Connection(Provider provider) {
        this.provider = provider;
    }

    public boolean isConnected(){
        if ( transport != null && transport.isConnected()){
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
        return transport;
    }

    public void setTransport(final ClientTransport transport) {
        this.transport = transport;
        transport.addChangeListener(new ClientTransportListener() {
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

    @Override
    public String toString() {
        return transport != null ?
                NetUtil.channelToString(transport.getLocalAddress(),transport.getRemoteAddress()) :
                provider.toString();
    }
}
