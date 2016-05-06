package com.jiaxy.ssf.processor;

import com.jiaxy.ssf.intercept.MessageInvocation;
import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     execute provider service logic<br>
 *
 *     execute filtered by the interceptors
 *
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/07 10:54
 */
public class ProviderProcessor extends MessageInvocationProcessor {

    public ProviderProcessor(MessageInvocation invocation) {
        super(invocation);
    }

    @Override
    public ResponseMessage execute(RequestMessage message) throws Throwable{
        //clone the message invocation
        return invocationClone().proceed(message);
    }
}
