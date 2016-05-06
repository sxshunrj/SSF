package com.jiaxy.ssf.intercept;

import com.jiaxy.ssf.config.ConsumerConfig;
import com.jiaxy.ssf.message.MessageBuilder;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     client send message here
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/06 17:10
 */
public class ConsumerMessageInterceptor implements MessageInterceptor {


    private ConsumerConfig consumerConfig;


    public ConsumerMessageInterceptor(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    @Override
    public ResponseMessage invoke(MessageInvocation invocation, RequestMessage message) throws Throwable {
        try {
            return consumerConfig.getClient().sendMsg(message);
        } catch (Exception e) {
            ResponseMessage res = MessageBuilder.buildResponseMessage(message);
            res.setException(e);
            return res;
        }
    }
}
