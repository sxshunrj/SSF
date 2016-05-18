package com.jiaxy.ssf.test.suite;

import com.jiaxy.ssf.config.ProviderConfig;
import com.jiaxy.ssf.config.ServerConfig;
import com.jiaxy.ssf.test.suite.impl.ServiceImpl;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/16 11:06
 */
public class ServiceMain {

    public static void main(String[] args){
        ProviderConfig<IService> providerConfig = new ProviderConfig<IService>();
        providerConfig.setAlias("ssf-test-suite");
        providerConfig.setServiceInterfaceName(IService.class.getCanonicalName());
        providerConfig.setRef(new ServiceImpl());
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setProtocol("ssf");
        serverConfig.setPort(31919);
        serverConfig.setEpoll(false);
        serverConfig.addProvider(providerConfig);
        serverConfig.start();
        synchronized (ServiceMain.class){
            while (true){
                try {
                    ServiceMain.class.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
