package com.jiaxy.ssf.regcenter.server;

import com.jiaxy.ssf.regcenter.common.*;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/13 11:10
 */
public class RCServer {

    private static final Logger logger = LoggerFactory.getLogger(RCServer.class);

    public static void main(String[] args) throws InterruptedException {

        String host = NetUtil.getLocalHost();
        int port = NetUtil.tryAvailablePort(host,61800);
        String logDirectory = System.getProperty("user.home")+ File.separator+"rcserver"+File.separator+port;
        Address address = new Address(host,NetUtil.tryAvailablePort(host, NetUtil.tryAvailablePort(host, port)));
        List<Address> members = new ArrayList<>();
        for (int i = 0;i < 3 ;i++){
            Address member = new Address(host,61800 + i);
            members.add(member);
        }
        CopycatServer server = CopycatServer.builder(address)
          .withStateMachine(RegistrationStateMachine::new)
          .withTransport(new NettyTransport())
          .withStorage(Storage.builder()
                  .withDirectory(logDirectory)
                  .withMaxSegmentSize(1024 * 1024 * 32)
                  .withCompactionThreshold(0.0001)
                  .withMinorCompactionInterval(Duration.ofSeconds(5))
                  .withMajorCompactionInterval(Duration.ofMinutes(1))
                  .build())
          .build();
        server.serializer().register(RegisterCommand.class,1);
        server.serializer().register(UnRegisterCommand.class,2);
        server.serializer().register(SubscribeCommand.class,3);
        server.serializer().register(UnSubscribeCommand.class,4);
        server.bootstrap(members).join();
        while (server.isRunning()){
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
