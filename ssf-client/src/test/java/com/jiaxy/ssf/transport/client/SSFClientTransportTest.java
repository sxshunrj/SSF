package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.config.ServerTransportConfig;
import com.jiaxy.ssf.message.MsgFuture;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.transport.server.SSFServerTransport;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SSFClientTransportTest {


    private SSFServerTransport ssfServerTransport;

    private ClientTransport ssfClientTransport;

    @Before
    public void setUp() throws Exception {
        ssfServerTransport = new SSFServerTransport(new ServerTransportConfig());
        ssfServerTransport.start();
        ssfClientTransport = ClientTransportFactory.getClientTransport(new ClientTransportFactory.ClientTransportKey(ProtocolType.SSF,"localhost",31616),new ClientTransportConfig());
    }

    @After
    public void tearDown() throws Exception {
        ssfServerTransport.stop();
    }

    @Test
    public void testDoSendAsync() throws Exception {
        MsgFuture msgFuture = ssfClientTransport.sendAsync(new RequestMessage(), 5000);
        Object rs = null;
        try {
            rs = msgFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(rs);
    }

    @Test
    public void testConnect() throws Exception {
        ssfClientTransport.connect();
    }

     @Test
    public void testIsConnected() throws Exception {
        Assert.assertEquals(true,ssfClientTransport.isConnected());
    }

    @Test
    public void testDisConnect() throws Exception {
        ssfClientTransport.disConnect();
    }


}