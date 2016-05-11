package com.jiaxy.ssf.processor;

import com.jiaxy.ssf.intercept.MessageInvocation;
import com.jiaxy.ssf.intercept.MessageInvocationFactory;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/06 18:52
 */
public abstract class MessageInvocationProcessor implements MessageProcessor {


    protected MessageInvocation invocation;


    public MessageInvocationProcessor(MessageInvocation invocation) {
        this.invocation = invocation;
    }

    protected MessageInvocation invocationClone(){
        MessageInvocation messageInvocation = MessageInvocationFactory.getMessageInvocation(invocation.interceptors());
        return messageInvocation;
    }
}
