package com.jiaxy.ssf.transport.client;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.config.ClientTransportConfig;
import com.jiaxy.ssf.config.ServerTransportConfig;
import com.jiaxy.ssf.transport.server.SSFServerTransport;
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
        System.out.println("-----------start------------");
    }

    @After
    public void tearDown() throws Exception {
        ssfServerTransport.stop();
        System.out.println("-----------stop------------");

    }

    @Test
    public void testDoSendAsync() throws Exception {

    }

    @Test
    public void testConnect() throws Exception {
        ssfClientTransport.connect();
        TimeUnit.MINUTES.sleep(1);
        ssfClientTransport.disConnect();
        TimeUnit.MINUTES.sleep(1);

    }

    @Test
    public void testDisConnect() throws Exception {
        ssfClientTransport.disConnect();
    }

    @Test
    public void testIsConnected() throws Exception {

    }
}