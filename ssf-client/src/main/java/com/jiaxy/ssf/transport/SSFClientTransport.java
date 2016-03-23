package com.jiaxy.ssf.transport;

import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.message.MsgFuture;
import com.jiaxy.ssf.message.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/22 11:17
 */
public class SSFClientTransport extends AbstractTcpClientTransport {

    private Logger logger = LoggerFactory.getLogger(SSFClientTransport.class);


    public SSFClientTransport(ClientTransportConfig clientTransportConfig) {
        super(clientTransportConfig);
    }

    @Override
    MsgFuture doSendAsync(RequestMessage msg, int timeout) {
        return null;
    }

    @Override
    public void connect() {

    }

    @Override
    public void disConnect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }
}
