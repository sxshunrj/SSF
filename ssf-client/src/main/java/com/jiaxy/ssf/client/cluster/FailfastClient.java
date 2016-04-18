package com.jiaxy.ssf.client.cluster;

import com.jiaxy.ssf.client.AbstractClient;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 10:17
 */
public class FailfastClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(FailfastClient.class);

    @Override
    protected ResponseMessage doSendMsg(RequestMessage requestMessage) {
        return null;
    }



    @Override
    public void close() {

    }
}
