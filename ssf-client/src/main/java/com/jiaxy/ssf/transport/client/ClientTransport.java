package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.message.MsgFuture;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;

import java.net.InetSocketAddress;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     handle the connection between client and the server
 * </p>
 *
 * @since 2016/03/16 17:56
 */
public interface ClientTransport {


    void connect();

    void disConnect();




    /**
     * send the message synchronized
     *
     * @param msg request message
     * @param timeout request timeout
     * @return
     */
    ResponseMessage sendSync(RequestMessage msg,int timeout);


    /**
     *
     * send the message asynchronously
     *
     * @param msg request message
     * @param timeout request timeout
     * @return
     */
    MsgFuture sendAsync(RequestMessage msg,int timeout);


    /**
     *
     * @return true if the connection is connected
     */
    boolean isConnected();


    /**
     *
     * @return the remote address
     */
    InetSocketAddress getRemoteAddress();


    /**
     *
     * @return the local address
     */
    InetSocketAddress getLocalAddress();


    /**
     *
     * @return the config of the client transport
     */
    ClientTransportConfig getClientTransportConfig();


    /**
     *
     * @return the number of the current request by this connection
     */
    int currentRequests();


    void addChangeListener(ClientTransportListener listener);


}
