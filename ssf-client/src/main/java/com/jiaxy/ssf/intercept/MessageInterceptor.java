package com.jiaxy.ssf.intercept;

import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/07 14:02
 */
public interface MessageInterceptor extends Interceptor {


    ResponseMessage invoke(MessageInvocation invocation,RequestMessage message) throws Throwable;
}
