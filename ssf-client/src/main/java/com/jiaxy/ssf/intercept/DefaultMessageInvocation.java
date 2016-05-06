package com.jiaxy.ssf.intercept;

import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @since 2016/04/07 15:04
 */
public class DefaultMessageInvocation implements MessageInvocation {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageInvocation.class);

    private int currentInterceptorIndex = -1;

    private final List<MessageInterceptor> interceptors;

    public DefaultMessageInvocation(List<MessageInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public ResponseMessage proceed(AbstractMessage message) throws Throwable{
        if ( currentInterceptorIndex > interceptors.size() - 1 ){
            throw new RpcException("the interceptor chain is not correct.please check it");
        }
        ResponseMessage responseMessage = null;
        MessageInterceptor messageInterceptor = interceptors.get(++currentInterceptorIndex);
        if ( currentInterceptorIndex == interceptors.size() - 1 ){
            //last interceptor execute service
            responseMessage = messageInterceptor.invoke(this,(RequestMessage)message);
            return responseMessage;
        } else {
            responseMessage = messageInterceptor.invoke(this,(RequestMessage)message);
        }
        return responseMessage;
    }

    @Override
    public List<MessageInterceptor> interceptors() {
        return interceptors;
    }
}
