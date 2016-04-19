package com.jiaxy.ssf.client.cluster;

import com.jiaxy.ssf.client.AbstractClient;
import com.jiaxy.ssf.config.ConsumerConfig;
import com.jiaxy.ssf.connection.Connection;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.RequestMessageBody;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.registry.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     fail fast no retry
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 10:17
 */
public class FailfastClient extends AbstractClient {

    public FailfastClient(ConsumerConfig consumerConfig) {
        super(consumerConfig);
    }

    @Override
    protected ResponseMessage doSendMsg(RequestMessage requestMessage) {
        String method = requestMessage.getMethodName();
        RequestMessageBody reqBody = requestMessage.getRequestMessageBody();
        ResponseMessage responseMessage = null;
        Connection connection = super.selectConnection(requestMessage, null);
        try {
            responseMessage = super.sendMsg0(requestMessage, connection);
            if (responseMessage != null) {
                return responseMessage;
            } else {
                throw new RpcException(String.format("%s/%s/%s:invoked failed,response message should not null",
                        reqBody.getClassName(),
                        reqBody.getAlias(),
                        method
                ));
            }
        } catch (Throwable e) {
             throw  new RpcException(String.format("%s/%s/%s:invoked failed,remote:%s,Cause by %s.message is:%s",
                    reqBody.getClassName(),
                    reqBody.getAlias(),
                    method,
                    connection.getProvider(),
                    e.getClass().getCanonicalName(),
                    e.getMessage()
            ), e);
        }
    }

}
