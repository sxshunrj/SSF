package com.jiaxy.ssf.processor;

import com.jiaxy.ssf.config.ConsumerConfig;
import com.jiaxy.ssf.intercept.MessageInvocation;
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
 * @since 2016/04/15 18:35
 */
public class ConsumerProcessor extends MessageInvocationProcessor {

    private ConsumerConfig consumerConfig;

    public ConsumerProcessor(MessageInvocation invocation, ConsumerConfig consumerConfig) {
        super(invocation);
        this.consumerConfig = consumerConfig;
    }

    @Override
    public ResponseMessage execute(RequestMessage in) throws Throwable {
        in.getRequestMessageBody().setAlias(consumerConfig.getAlias());
        return invocationClone().proceed(in);
    }
}
