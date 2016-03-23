package com.jiaxy.ssf.transport;

import com.jiaxy.ssf.config.ServerTransportConfig;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SSFServerTransportTest {

    private ServerTransportConfig config;

    private SSFServerTransport ssfServerTransport;

    @org.junit.Before
    public void setUp() throws Exception {
        config = new ServerTransportConfig();
        ssfServerTransport = new SSFServerTransport(config);
    }

    @org.junit.Test
    public void testStart() throws Exception {
        assertEquals(true,ssfServerTransport.start());
    }

    @org.junit.Test
    public void testStop() throws Exception {
        assertEquals(true,ssfServerTransport.stop());
    }
}