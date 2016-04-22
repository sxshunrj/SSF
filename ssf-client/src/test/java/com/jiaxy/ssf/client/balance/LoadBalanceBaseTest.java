package com.jiaxy.ssf.client.balance;

import com.jiaxy.ssf.registry.Provider;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/22 11:09
 */
public class LoadBalanceBaseTest {


    protected List<Provider> providers = new ArrayList<Provider>();


    protected ConcurrentHashMap<Provider,AtomicInteger> statistics = new ConcurrentHashMap<Provider, AtomicInteger>();

    @Before
    public void setUp() throws Exception {
        providers.add(new Provider("172.0.0.1",31616,100));
        providers.add(new Provider("172.0.0.2",31616,200));
        providers.add(new Provider("172.0.0.3",31616,300));
        providers.add(new Provider("172.0.0.4",31616,400));
        providers.add(new Provider("172.0.0.5",31616,500));
        providers.add(new Provider("172.0.0.6",31616,600));
        providers.add(new Provider("172.0.0.7",31616,700));
    }



    protected void report(){
        for (Map.Entry<Provider,AtomicInteger> entry : statistics.entrySet()){
            Provider provider = entry.getKey();
            System.out.print(provider.getIp()+":"+provider.getPort()+" | invoked:");
            System.out.println(entry.getValue().get());
        }
    }
}
