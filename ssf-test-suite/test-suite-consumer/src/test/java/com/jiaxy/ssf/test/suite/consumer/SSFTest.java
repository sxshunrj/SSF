package com.jiaxy.ssf.test.suite.consumer;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.config.ConsumerConfig;
import com.jiaxy.ssf.service.Callback;
import com.jiaxy.ssf.test.suite.IService;
import com.jiaxy.ssf.util.SSFContext;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/16 11:17
 */
public class SSFTest {


    private Callback<String,String> callback = new Callback<String, String>() {
        @Override
        public String callback(String ipo) {
//            throw new RuntimeException("consumer exception");
            return SSFContext.getLocalHost()+":consumer executed";
        }
    };



    @Test
    public void testCallback(){
        ConsumerConfig<IService> consumerConfig = new ConsumerConfig<IService>();
        consumerConfig.setServiceInterfaceName(IService.class.getCanonicalName());
        consumerConfig.setAlias("ssf-test-suite");
        consumerConfig.setUrl("ssf://127.0.0.1:31919?weight=200,ssf://127.0.0.1:31919");
        consumerConfig.setProtocol(ProtocolType.SSF);
        consumerConfig.setRetries(0);
        consumerConfig.setTimeout(1000);
        IService service = consumerConfig.refer();
        try {
            String rs = service.registryWithCallback("ssf", callback);
            Assert.assertEquals(SSFContext.getLocalHost()+":consumer executed message from service", rs);
        } catch (Throwable e) {
            e.printStackTrace();
        }
//        rs = service.registryWithCallback("ssf", callback);
//        Assert.assertEquals("callback:hello ssf message from service",rs);
    }
}
