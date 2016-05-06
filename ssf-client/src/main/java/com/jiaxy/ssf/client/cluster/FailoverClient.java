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
 *     fail over client</br>
 *     retry by the consumer config retries config value
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 10:17
 */
public class FailoverClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(FailoverClient.class);


    public FailoverClient(ConsumerConfig consumerConfig) {
        super(consumerConfig);
    }

    @Override
    protected ResponseMessage doSendMsg(RequestMessage requestMessage) {
        String method = requestMessage.getMethodName();
        RequestMessageBody reqBody = requestMessage.getRequestMessageBody();
        int retries = consumerConfig.getRetriesNumber(method);
        Throwable lastError = null;
        List<Provider> invokedProviders = new ArrayList<Provider>();
        ResponseMessage responseMessage = null;
        //retried time
        int retried = 0;
        do {
            Connection connection = super.selectConnection(requestMessage, invokedProviders);
            try {
                invokedProviders.add(connection.getProvider());
                responseMessage = super.sendMsg0(requestMessage,connection);
                if (responseMessage != null){
                    if (lastError != null){
                        logger.warn("invoked successfully retried {} time,last error:",retried,lastError.getMessage());
                    }
                    return responseMessage;
                } else {
                    lastError = new RpcException(String.format("%s/%s/%s:invoked failed,response message should not null",
                            reqBody.getClassName(),
                            reqBody.getAlias(),
                            method
                            ));
                }
            } catch (RpcException e){
                lastError = e;
            } catch (Throwable e){
                lastError = new RpcException(String.format("%s/%s/%s:invoked failed,remote:%s,Cause by %s.message is:%s",
                            reqBody.getClassName(),
                            reqBody.getAlias(),
                            method,
                            connection.getProvider(),
                            e.getClass().getCanonicalName(),
                            e.getMessage()
                            ),e);
            }
            retried++;
        } while (retried < retries);
        if (retries == 0){
            throw new RpcException(String.format("%s/%s/%s:invoked failed,remote:%s,Cause by %s.message is:%s",
                            reqBody.getClassName(),
                            reqBody.getAlias(),
                            method,
                            invokedProviders,
                            lastError.getClass().getCanonicalName(),
                            lastError.getMessage()
                            ),lastError);
        } else {
             throw new RpcException(String.format("%s/%s/%s:invoked failed include retried %d time,remote:%s,Cause by %s.message is:%s",
                            reqBody.getClassName(),
                            reqBody.getAlias(),
                            method,
                            retries,
                            invokedProviders,
                            lastError.getClass().getCanonicalName(),
                            lastError.getMessage()
                            ),lastError);

        }
    }
}
