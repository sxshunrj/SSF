package com.jiaxy.ssf.server;

import com.jiaxy.ssf.config.ProviderConfig;
import com.jiaxy.ssf.config.RegistryConfig;
import com.jiaxy.ssf.config.ServerConfig;
import com.jiaxy.ssf.service.TestSuiteService;
import com.jiaxy.ssf.service.impl.TestSuiteServiceImpl;
import com.jiaxy.ssf.util.NetUtil;
import org.junit.Test;

import java.util.Arrays;

public class ServerTest {

    @Test
    public void testStart() throws Exception {
        ProviderConfig<TestSuiteService> providerConfig = new ProviderConfig<TestSuiteService>();
        providerConfig.setAlias("ssf-test-suite");
        providerConfig.setServiceInterfaceName("com.jiaxy.ssf.service.TestSuiteService");
        providerConfig.setRef(new TestSuiteServiceImpl());
        RegistryConfig registryConfig = new RegistryConfig();
        String host = NetUtil.getLocalHost();
        registryConfig.setRegisterAddresses(Arrays.asList(String.format("%s:61800",host),String.format("%s:61801",host),String.format("%s:61802",host)));
        providerConfig.setRegistries(Arrays.asList(registryConfig));
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setProtocol("ssf");
        serverConfig.setPort(31818);
        serverConfig.setEpoll(false);
        serverConfig.addProvider(providerConfig);
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