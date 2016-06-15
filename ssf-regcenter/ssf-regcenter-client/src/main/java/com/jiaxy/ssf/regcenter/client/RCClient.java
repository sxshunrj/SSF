package com.jiaxy.ssf.regcenter.client;

import com.jiaxy.ssf.common.bo.SSFURL;
import com.jiaxy.ssf.common.bo.SubscribeURL;
import com.jiaxy.ssf.regcenter.common.RegisterCommand;
import com.jiaxy.ssf.regcenter.common.SubscribeCommand;
import com.jiaxy.ssf.regcenter.common.UnRegisterCommand;
import com.jiaxy.ssf.regcenter.common.UnSubscribeCommand;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.ConnectionStrategies;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.client.RecoveryStrategies;
import io.atomix.copycat.client.ServerSelectionStrategies;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/13 13:51
 */
public class RCClient {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String host = NetUtil.getLocalHost();
        List<Address> members = new ArrayList<>();
        for (int i = 0;i < 3 ;i++){
            Address member = new Address(host, 61800 + i);
            members.add(member);
        }

        CopycatClient client = CopycatClient.builder()
                .withTransport(new NettyTransport())
                .withConnectionStrategy(ConnectionStrategies.FIBONACCI_BACKOFF)
                .withRecoveryStrategy(RecoveryStrategies.RECOVER)
                .withServerSelectionStrategy(ServerSelectionStrategies.LEADER)
                .build();

        client.serializer().register(RegisterCommand.class,1);
        client.serializer().register(UnRegisterCommand.class,2);
        client.serializer().register(SubscribeCommand.class,3);
        client.serializer().register(UnSubscribeCommand.class,4);
        client.connect(members).join();
        SSFURL ssfurl = new SSFURL();
        String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        System.out.println(PID);
        ssfurl.setIp(host);
        ssfurl.setPort(31616);
        ssfurl.setServiceName("com.jiaxy.ssf.service.TestSuiteService");
        ssfurl.setAlias("test-ssf-2");
//        ssfurl.setPid(Integer.valueOf(PID));
        ssfurl = client.submit(new RegisterCommand(ssfurl)).get();
//        boolean rs = client.submit(new UnRegisterCommand(ssfurl)).get();
//        System.out.println(ssfurl.getStartTime());
//        System.out.println(rs);
        SubscribeURL subscribeURL = new SubscribeURL();
        subscribeURL.setSourceURL(ssfurl);
        SubscribeURL subscribedURL = client.submit(new SubscribeCommand(subscribeURL)).get();
//        subscribedURL.getProviderList().forEach(System.out::println);
        client.onEvent("provider.add",newSSFURL -> System.out.println(newSSFURL));
//        client.close();

    }
}
