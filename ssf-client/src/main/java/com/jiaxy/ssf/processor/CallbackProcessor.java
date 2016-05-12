package com.jiaxy.ssf.processor;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.transport.client.ClientTransport;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/11 10:17
 */
public class CallbackProcessor implements MessageProcessor {

    private ClientTransport clientTransport;


    public CallbackProcessor(ClientTransport clientTransport) {
        this.clientTransport = clientTransport;
    }

    @Override
    public ResponseMessage execute(RequestMessage in) throws Throwable {
        in.getHead().setProtocolType(ProtocolType.SSF.getValue());
        in.getHead().setMessageType(AbstractMessage.CALLBACK_REQUEST_MSG);
        return clientTransport.sendSync(in,5000);//5s
    }
}
