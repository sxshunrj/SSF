package com.jiaxy.ssf.processor;

import com.jiaxy.ssf.intercept.MessageInvocation;
import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.ResponseMessage;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     execute provider service logic<br>
 *
 *     execute filter by the interceptors
 *
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/07 10:54
 */
public class ProviderProcessor implements Processor<AbstractMessage,ResponseMessage> {

    private MessageInvocation invocation;

    public ProviderProcessor(MessageInvocation invocation) {
        this.invocation = invocation;
    }

    @Override
    public ResponseMessage execute(AbstractMessage message) throws Throwable{
        //clone the message invocation
        return invocation.clone().proceed(message);
    }
}
