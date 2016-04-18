package com.jiaxy.ssf.connection;

import com.jiaxy.ssf.registry.Provider;
import com.jiaxy.ssf.transport.client.ClientTransport;

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
public class Connection {

    private Provider provider;

    private ClientTransport transport;


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

    public void setTransport(ClientTransport transport) {
        this.transport = transport;
    }
}
