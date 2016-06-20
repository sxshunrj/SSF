package com.jiaxy.ssf.regcenter.client.copycat;

import com.jiaxy.ssf.common.bo.SSFURL;
import com.jiaxy.ssf.common.bo.SubscribeURL;
import com.jiaxy.ssf.regcenter.common.Constants;
import com.jiaxy.ssf.regcenter.common.NetUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CopycatRegClientTest {


    private SSFURL ssfurl;

    private SubscribeURL subscribeURL;

    private CopycatRegClient copycatRegClient;

    @Before
    public void setUp() throws Exception {
        String host = NetUtil.getLocalHost();
        ssfurl = new SSFURL();
        ssfurl.setIp(host);
        ssfurl.setPort(31616);
        ssfurl.setServiceName("com.jiaxy.ssf.service.TestSuiteService");
        ssfurl.setAlias("test-ssf-1");
        subscribeURL = new SubscribeURL();
        subscribeURL.setSourceURL(ssfurl);
        List<String> rcServerAddress = new ArrayList<>();
        for (int i = 0;i < 3 ;i++){
            rcServerAddress.add(host + ":" + (61800 + i));
        }
        copycatRegClient = new CopycatRegClient(rcServerAddress);
        copycatRegClient.onDiscoveryEvent(Constants.PROVIDER_ADD_EVENT,ssfurl -> {
            System.out.println("add provider:"+ssfurl);
        });
        copycatRegClient.onDiscoveryEvent(Constants.PROVIDER_REMOVE_EVENT,ssfurl -> {
            System.out.println("remove provider:"+ssfurl);
        });

    }

    @org.junit.Test
    public void testRegister() throws Exception {
        SSFURL registeredURL = copycatRegClient.register(ssfurl);
        System.out.println(registeredURL.getStartTime());
        assertNotNull(registeredURL.getStartTime());

    }

    @org.junit.Test
    public void testUnRegister() throws Exception {
        boolean rs = copycatRegClient.unRegister(ssfurl);
        assertTrue(rs);
    }

    @org.junit.Test
    public void testSubscribe() throws Exception {
        SubscribeURL subscribedURL = copycatRegClient.subscribe(subscribeURL);
        if (subscribedURL.getProviderList() != null){
            subscribedURL.getProviderList().forEach(System.out::println);
        }
    }

    @org.junit.Test
    public void testUnSubscribe() throws Exception {
        boolean rs = copycatRegClient.unSubscribe(subscribeURL);
        assertTrue(rs);
    }

    @Test
    public void testDiscoveryEvent() throws Exception {
        testSubscribe();
        synchronized (this){
            this.wait();
        }
    }
}