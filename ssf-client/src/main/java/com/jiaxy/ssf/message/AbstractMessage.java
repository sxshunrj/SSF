package com.jiaxy.ssf.message;

import io.netty.buffer.ByteBuf;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 14:48
 */
public abstract class AbstractMessage {

    //-----------------Message Type----------------------
    public static final int REQUEST_MSG = 1;

    public static final int RESPONSE_MSG = 2;

    public static final int HEARTBEAT_REQUEST_MSG = 3;

    public static final int HEARTBEAT_RESPONSE_MSG = 4;

    public static final int CALLBACK_REQUEST_MSG = 5;

    public static final int CALLBACK_RESPONSE_MSG = 6;

    private MessageHead head;

    private ByteBuf msg;

    private ByteBuf msgBody;


    protected AbstractMessage(boolean initMessageHead){
        if ( initMessageHead ){
            head = new MessageHead();
        }
    }

    public int getMsgId(){
        if ( head != null ){
            return head.getMsgId();
        } else {
            return -1;
        }
    }

    /**
     *
     * @return true if the message is heartbeat
     */
    public boolean isHeartBeatRequestMsg(){
        int msgType = getHead().getMessageType();
        if ( msgType == HEARTBEAT_REQUEST_MSG){
            return true;
        }
        return false;
    }

    /**
     *
     * @return true if the message is heartbeat response
     */
    public boolean isHeartBeatResponseMsg(){
        int msgType = getHead().getMessageType();
        if ( msgType == HEARTBEAT_RESPONSE_MSG){
            return true;
        }
        return false;
    }


    /**
     *
     *
     * @return
     */
    public boolean isCallbackRequestMsg(){
        int msgType = getHead().getMessageType();
        if ( msgType == CALLBACK_REQUEST_MSG ){
            return true;
        }
        return false;
    }

    public MessageHead getHead() {
        return head;
    }

    public void setHead(MessageHead head) {
        this.head = head;
    }

    public ByteBuf getMsg() {
        return msg;
    }

    public void setMsg(ByteBuf msg) {
        this.msg = msg;
    }

    public ByteBuf getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(ByteBuf msgBody) {
        this.msgBody = msgBody;
    }

    @Override
    public String toString() {
        return "AbstractMessage{" +
                "head=" + head +
                '}';
    }
}
