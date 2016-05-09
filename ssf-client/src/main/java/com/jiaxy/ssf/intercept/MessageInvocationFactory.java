package com.jiaxy.ssf.intercept;

import com.jiaxy.ssf.config.ConsumerConfig;
import com.jiaxy.ssf.config.ProviderConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/11 18:15
 */
public class MessageInvocationFactory {


    public static MessageInvocation getMessageInvocation(ProviderConfig providerConfig){
        MessageInvocation defaultMessageInvocation = new DefaultMessageInvocation(buildProviderInterceptors(providerConfig));
        return defaultMessageInvocation;
    }

    public static MessageInvocation getMessageInvocation(List<MessageInterceptor> interceptors){
        MessageInvocation defaultMessageInvocation = new DefaultMessageInvocation(interceptors);
        return defaultMessageInvocation;
    }

    public static MessageInvocation getMessageInvocation(ConsumerConfig consumerConfig){
        MessageInvocation defaultMessageInvocation = new DefaultMessageInvocation(buildConsumerInterceptors(consumerConfig));
        return defaultMessageInvocation;
    }


    private static List<MessageInterceptor> buildProviderInterceptors(ProviderConfig providerConfig){
        List<MessageInterceptor> interceptors = new ArrayList<MessageInterceptor>();
        if (providerConfig.getInterceptors() != null && !providerConfig.getInterceptors().isEmpty()){
            interceptors.addAll(providerConfig.getInterceptors());
        }
        //last interceptor.must in the end
        interceptors.add(new ServiceReflectiveMessageInterceptor(providerConfig));
        return interceptors;
    }

    private static List<MessageInterceptor> buildConsumerInterceptors(ConsumerConfig consumerConfig) {
        List<MessageInterceptor> interceptors = new ArrayList<MessageInterceptor>();
        interceptors.add(new ExceptionInterceptor());
        if (consumerConfig.getInterceptors() != null && !consumerConfig.getInterceptors().isEmpty()){
            interceptors.addAll(consumerConfig.getInterceptors());
        }
        //last interceptor is the invoke
        interceptors.add(new ConsumerMessageInterceptor(consumerConfig));
        return interceptors;
    }
}
