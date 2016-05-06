package com.jiaxy.ssf.config;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.service.TestSuiteService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConsumerConfigTest {

    private ConsumerConfig<TestSuiteService> consumerConfig;

    @Before
    public void setUp() throws Exception {
        consumerConfig = new ConsumerConfig<TestSuiteService>();
        consumerConfig.setServiceInterfaceName(TestSuiteService.class.getCanonicalName());
        consumerConfig.setAlias("ssf-test-suite");
        consumerConfig.setUrl("ssf://127.0.0.1:31818?weight=200,ssf://127.0.0.1:31617");
        consumerConfig.setProtocol(ProtocolType.SSF);
        consumerConfig.setRetries(0);
    }

    @Test
    public void testRefer() throws Exception {
        TestSuiteService suiteService = consumerConfig.refer();
        String rs = suiteService.helloWorld("wutao");
        System.out.println(rs);

    }

    @Test
    public void testUnRefer() throws Exception {

    }
}