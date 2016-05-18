package com.jiaxy.ssf.test.suite.consumer;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.config.ConsumerConfig;
import com.jiaxy.ssf.test.suite.IService;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/16 11:13
 */
public class ConsumerMain {

    public static void main(String[] args){
        ConsumerConfig<IService> consumerConfig = new ConsumerConfig<IService>();
        consumerConfig.setServiceInterfaceName(IService.class.getCanonicalName());
        consumerConfig.setAlias("ssf-test-suite");
        consumerConfig.setUrl("ssf://127.0.0.1:31919?weight=200,ssf://127.0.0.1:31919");
        consumerConfig.setProtocol(ProtocolType.SSF);
        consumerConfig.setRetries(0);

    }
}
