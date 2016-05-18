package com.jiaxy.ssf.processor;

import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.*;
import com.jiaxy.ssf.proxy.ServiceProxyFactory;
import com.jiaxy.ssf.transport.client.ClientTransport;
import com.jiaxy.ssf.util.NetUtil;

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

    private String callbackInstanceId;

    public CallbackProcessor(ClientTransport clientTransport, String callbackInstanceId) {
        this.clientTransport = clientTransport;
        this.callbackInstanceId = callbackInstanceId;
    }

    public ResponseMessage execute(RequestMessage in) throws Throwable {
        in.getHead().setProtocolType(ProtocolType.SSF.getValue());
        in.getHead().setMessageType(AbstractMessage.CALLBACK_REQUEST_MSG);
        in.getHead().addHeadKey(MessageHead.HeadKey.CALLBACK_INSTANCE_ID,callbackInstanceId);
        if (!clientTransport.isConnected()){
            ServiceProxyFactory.removeCallbackProxy(callbackInstanceId);
            ResponseMessage responseMessage = MessageBuilder.buildResponseMessage(in);
            responseMessage.getHead().setMessageType(AbstractMessage.CALLBACK_RESPONSE_MSG);
            responseMessage.setException(new RpcException(String.format("send callback request failed.channel %s is inactive",
                    NetUtil.channelToString(clientTransport.getLocalAddress(),clientTransport.getRemoteAddress()))));
            return responseMessage;
        }
        return clientTransport.sendSync(in,5000);//5s
    }
}
