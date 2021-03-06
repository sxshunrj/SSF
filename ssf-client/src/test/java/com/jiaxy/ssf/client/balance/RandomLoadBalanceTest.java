package com.jiaxy.ssf.client.balance;

import com.jiaxy.ssf.client.LoadBalance;
import com.jiaxy.ssf.message.MessageBuilder;
import com.jiaxy.ssf.registry.Provider;
import com.jiaxy.ssf.service.TestSuiteService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomLoadBalanceTest extends LoadBalanceBaseTest {


    @Test
    public void testSelect() throws Exception {
        LoadBalance balance = new RandomLoadBalance();
        for ( int i = 0 ;i< 100;i++){
            Provider provider = balance.select(MessageBuilder.buildRequestMessage(TestSuiteService.class, "echo", new Class[0], new Object[0]), providers);
            AtomicInteger selected = statistics.putIfAbsent(provider,new AtomicInteger(0));
            if (selected == null){
                selected = statistics.get(provider);
            }
            selected.incrementAndGet();
        }
        report();
    }

}