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
     * 接受请求时间,即创建时间
     */
    private long receivedTime;

    private String remoteAddress;

    private MessageBody messageBody;


    public RequestMessage(boolean initMessageHead) {
        super(initMessageHead);
    }

    public RequestMessage(){
        super(true);
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

    public MessageBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(MessageBody messageBody) {
        this.messageBody = messageBody;
    }

    public void setRequestMsgId(Integer msgId){
        if ( getHead() != null ){
            getHead().setMsgId(msgId);
        }
    }
}
