package com.jiaxy.ssf.config;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.intercept.MessageInterceptor;
import com.jiaxy.ssf.intercept.MessageInvocation;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.service.TestSuiteService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

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
        String rs = suiteService.helloWorld("");
        assertEquals(" ssf hello world!", rs);

    }

    @Test
    public void testConnectionManager() throws Exception {
        TestSuiteService suiteService = consumerConfig.refer();
         synchronized (this){
            while (true){
                try {
                    this.wait();
                }catch (Exception e){
                }
            }
        }
    }

    @Test
    public void testInterceptors() throws Exception {
        consumerConfig.setInterceptors(Arrays.asList(new BeforeInterceptors(),new AfterInterceptors()));
        TestSuiteService suiteService = consumerConfig.refer();
        String rs = suiteService.helloWorld("");
        assertEquals("after ssf hello world!", rs);
        rs = suiteService.echo();
        Assert.assertEquals("afterecho", rs);
    }

    @Test
    public void testUnRefer() throws Exception {

    }


    class BeforeInterceptors implements MessageInterceptor{
        @Override
        public ResponseMessage invoke(MessageInvocation invocation, RequestMessage message) throws Throwable {
            System.out.println("---before---");
            return invocation.proceed(message);
        }
    }

    class AfterInterceptors implements MessageInterceptor{
        @Override
        public ResponseMessage invoke(MessageInvocation invocation, RequestMessage message) throws Throwable {
            ResponseMessage res = invocation.proceed(message);
            res.setResponse("after"+res.getResponse());
            return res;
        }
    }
}