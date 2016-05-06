package com.jiaxy.ssf.message;

import com.jiaxy.ssf.common.ClassUtil;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/06 15:00
 */
public class MessageBuilder {


    public static ResponseMessage buildHeartbeatResponse(RequestMessage requestMessage){
        ResponseMessage responseMessage = new ResponseMessage(false);
        responseMessage.setHead(requestMessage.getHead().clone());
        responseMessage.getHead().setMessageType(AbstractMessage.HEARTBEAT_RESPONSE_MSG);
        return responseMessage;
    }

    public static ResponseMessage buildResponseMessage(RequestMessage requestMessage){
        ResponseMessage responseMessage = new ResponseMessage(false);
        responseMessage.setHead(requestMessage.getHead().clone());
        return responseMessage;
    }


    public static RequestMessage buildRequestMessage(Class serviceInterfaceClz,
                                                     String methodName,
                                                     Class[] paramTypes,
                                                     Object[] args){
        RequestMessage requestMessage = new RequestMessage();
        RequestMessageBody body = new RequestMessageBody();
        body.setArgs(args);
        body.setArgsType(ClassUtil.classArr2StringArr(paramTypes));
        body.setClassName(serviceInterfaceClz.getName());
        body.setMethodName(methodName);
        requestMessage.setRequestMessageBody(body);
        return requestMessage;
    }
}
