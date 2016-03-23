package com.jiaxy.ssf.config;

import com.jiaxy.ssf.common.Constants;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @since 2016/03/16 18:26
 */
public class ClientTransportConfig {


    private int connectionTimeout = Constants.DEFAULT_CLIENT_CONNECT_TIMEOUT;


    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
