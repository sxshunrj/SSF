package com.jiaxy.ssf.message;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     Request message
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 16:50
 */
public class RequestMessage extends AbstractMessage {

    /**
     * the time received the request
     */
    private long receivedTime;

    private String remoteAddress;

    private RequestMessageBody requestMessageBody;


    public RequestMessage(boolean initMessageHead) {
        super(initMessageHead);
        if ( getHead() != null ){
            getHead().setMessageType(REQUEST_MSG);
        }
    }

    public RequestMessage(){
        super(true);
        getHead().setMessageType(REQUEST_MSG);
    }

    /**
     *
     * @return time out config of the client call
     */
    public Integer getCallTimeout(){
        Integer timeout = (Integer) getHead().getAttrValue(MessageHead.CLIENT_CALL_TIMEOUT);
        if ( timeout == null ){
            return null;
        }
        return timeout;
    }


    public String getServiceName(){
        if ( requestMessageBody != null ){
            return requestMessageBody.getClassName();
        } else {
            return null;
        }
    }

    public String getMethodName(){
        if ( requestMessageBody != null ){
            return requestMessageBody.getMethodName();
        } else {
            return null;
        }
    }

    public String getAlias(){
        if ( requestMessageBody != null ){
            return requestMessageBody.getAlias();
        } else {
            return null;
        }
    }


    public long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public RequestMessageBody getRequestMessageBody() {
        return requestMessageBody;
    }

    public void setRequestMessageBody(RequestMessageBody requestMessageBody) {
        this.requestMessageBody = requestMessageBody;
    }

    public void setRequestMsgId(Integer msgId){
        if ( getHead() != null ){
            getHead().setMsgId(msgId);
        }
    }

    @Override
    public void setHead(MessageHead head) {
        super.setHead(head);
        if ( this.getHead() != null ){
            this.getHead().setMessageType(REQUEST_MSG);
        }

    }
}
