package com.jiaxy.ssf.server;

import com.jiaxy.ssf.config.ProviderConfig;
import com.jiaxy.ssf.config.ServerConfig;
import com.jiaxy.ssf.service.TestSuiteService;
import com.jiaxy.ssf.service.impl.TestSuiteServiceImpl;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ServerTest {

    @Test
    public void testStart() throws Exception {
        ProviderConfig<TestSuiteService> providerConfig = new ProviderConfig<TestSuiteService>();
        providerConfig.setAlias("ssf-test-suite");
        providerConfig.setServiceInterfaceName("com.jiaxy.ssf.service.TestSuiteService");
        providerConfig.setRef(new TestSuiteServiceImpl());
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setProtocol("ssf");
        serverConfig.setPort(31818);
        serverConfig.setEpoll(false);
        serverConfig.bindProvider(providerConfig);
        serverConfig.start();
        synchronized (this){
            while (true){
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Test
    public void testStop() throws Exception {

    }
}