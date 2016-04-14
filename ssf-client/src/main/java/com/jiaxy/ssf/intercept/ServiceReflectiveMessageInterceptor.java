package com.jiaxy.ssf.intercept;

import com.jiaxy.ssf.config.ProviderConfig;
import com.jiaxy.ssf.message.MessageBuilder;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.RequestMessageBody;
import com.jiaxy.ssf.message.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static com.jiaxy.ssf.common.ClassUtil.*;
import static com.jiaxy.ssf.common.ReflectUtil.invokeMethod;

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
    public ResponseMessage invoke(MessageInvocation invocation,RequestMessage message) throws Throwable {
        //reflect execute
        ResponseMessage responseMessage = reflectionInvoke(message);
        return responseMessage;
    }

    private ResponseMessage reflectionInvoke(RequestMessage message) throws ClassNotFoundException {
        RequestMessageBody body = message.getRequestMessageBody();
        String methodName = body.getMethodName();
        Object[] args = body.getArgs();
        ResponseMessage responseMessage = MessageBuilder.buildResponseMessage(message);
        Method method = getMethod(forName(body.getClassName(), getDefaultClassLoader()),
                methodName,
                forNames(body.getArgsType()));
        Object rs = invokeMethod(method, delegate, args);
        responseMessage.setResponse(rs);
        return responseMessage;
    }
}
