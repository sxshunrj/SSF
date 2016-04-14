package com.jiaxy.ssf.processor;

import com.jiaxy.ssf.intercept.MessageInvocation;
import com.jiaxy.ssf.intercept.MessageInvocationFactory;
import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     execute provider service logic
 *
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/07 10:54
 */
public class ProviderProcessor implements Processor<AbstractMessage,ResponseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(ProviderProcessor.class);

    private MessageInvocation invocation;

    public ProviderProcessor(MessageInvocation invocation) {
        this.invocation = invocation;
    }

    @Override
    public ResponseMessage execute(AbstractMessage message) throws Throwable{
        return invocation.proceed(message);
    }
}
