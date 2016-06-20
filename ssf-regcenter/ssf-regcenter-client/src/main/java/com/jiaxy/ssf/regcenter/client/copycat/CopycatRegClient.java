package com.jiaxy.ssf.regcenter.client.copycat;

import com.jiaxy.ssf.common.bo.SSFURL;
import com.jiaxy.ssf.common.bo.SubscribeURL;
import com.jiaxy.ssf.regcenter.DiscoveryEvent;
import com.jiaxy.ssf.regcenter.client.RegClient;
import com.jiaxy.ssf.regcenter.common.*;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.ConnectionStrategies;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.client.RecoveryStrategies;
import io.atomix.copycat.client.ServerSelectionStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/20 11:10
 */
public class CopycatRegClient implements RegClient {

    private static final Logger logger = LoggerFactory.getLogger(CopycatRegClient.class);

    private CopycatClient copycatClient;


    public CopycatRegClient(List<String> serverAddresses) {
        if (serverAddresses == null || serverAddresses.isEmpty()){
            throw new RuntimeException("reg server address can't be empty");
        }
        List<Address> members = serverAddresses.stream().map( addressStr -> {
            Function<String,Address> function = Address::new;
            return function.apply(addressStr);
        }).collect(Collectors.toList());
        copycatClient = CopycatClient.builder()
                .withTransport(new NettyTransport())
                .withConnectionStrategy(ConnectionStrategies.FIBONACCI_BACKOFF)
                .withRecoveryStrategy(RecoveryStrategies.RECOVER)
                .withServerSelectionStrategy(ServerSelectionStrategies.LEADER)
                .build();
        copycatClient.serializer().register(RegisterCommand.class,1);
        copycatClient.serializer().register(UnRegisterCommand.class,2);
        copycatClient.serializer().register(SubscribeCommand.class, 3);
        copycatClient.serializer().register(UnSubscribeCommand.class, 4);
        copycatClient.connect(members).join();

    }

    @Override
    public SSFURL register(SSFURL ssfurl) {
        try {
            ssfurl = copycatClient.submit(new RegisterCommand(ssfurl)).get();
        } catch (Throwable e) {
            logger.error("{} registered failed.",ssfurl,e);
        }
        return ssfurl;
    }

    @Override
    public boolean unRegister(SSFURL ssfurl) {
        try {
            return copycatClient.submit(new UnRegisterCommand(ssfurl)).get();
        } catch (Throwable e) {
            logger.error("{} unRegistered failed.",ssfurl,e);
            return false;
        }
    }

    @Override
    public SubscribeURL subscribe(SubscribeURL subscribeURL) {
        try {
            subscribeURL = copycatClient.submit(new SubscribeCommand(subscribeURL)).get();
        } catch (Throwable e){
            logger.error("subscribe {} failed.",subscribeURL,e);
        }
        return subscribeURL;
    }

    @Override
    public boolean unSubscribe(SubscribeURL subscribeURL) {
        try {
            return copycatClient.submit(new UnSubscribeCommand(subscribeURL)).get();
        } catch (Throwable e){
            logger.error("unSubscribe {} failed.",subscribeURL,e);
            return false;
        }
    }

    public void onDiscoveryEvent(String eventKey,DiscoveryEvent event){
        copycatClient.onEvent(eventKey,(SSFURL ssfURL) -> event.discovery(ssfURL));
    }
}
