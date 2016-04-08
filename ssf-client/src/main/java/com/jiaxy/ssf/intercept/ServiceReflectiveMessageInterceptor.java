package com.jiaxy.ssf.intercept;

import com.jiaxy.ssf.config.ProviderConfig;
import com.jiaxy.ssf.message.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     invoke service by reflectively.<br/>
 *
 *     the last interceptor of the chain
 *
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/07 15:32
 */
public class ServiceReflectiveMessageInterceptor<T> implements MessageInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ServiceReflectiveMessageInterceptor.class);

    private final T delegate;

    private final ProviderConfig<T> providerConfig;

    public ServiceReflectiveMessageInterceptor( ProviderConfig<T> providerConfig) {
        this.delegate = providerConfig.getRef();
        this.providerConfig = providerConfig;
    }

    @Override
    public ResponseMessage invoke(MessageInvocation invocation) throws Throwable {
        //reflect execute
        return null;
    }
}
