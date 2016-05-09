package com.jiaxy.ssf.intercept;

import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     Exception interceptor
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/06 18:44
 */
public class ExceptionInterceptor implements MessageInterceptor{

    @Override
    public ResponseMessage invoke(MessageInvocation invocation, RequestMessage message) throws Throwable {
        return invocation.proceed(message);
    }
}
