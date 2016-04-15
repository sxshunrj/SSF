package com.jiaxy.ssf.proxy;

import com.jiaxy.ssf.message.MessageBuilder;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.processor.MessageProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/15 17:16
 */
public class ProcessorInvocationHandler implements InvocationHandler {

    private MessageProcessor processor;


    public ProcessorInvocationHandler(MessageProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestMessage requestMessage = MessageBuilder.buildRequestMessage(method.getDeclaringClass(),
                method.getName(),
                method.getParameterTypes(),
                args);
        ResponseMessage responseMessage = processor.execute(requestMessage);
        if (responseMessage.isError()){
            throw responseMessage.getException();
        }
        return responseMessage.getResponse();
    }
}
