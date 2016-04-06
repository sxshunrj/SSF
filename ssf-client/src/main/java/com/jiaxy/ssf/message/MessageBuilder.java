package com.jiaxy.ssf.message;

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
}
