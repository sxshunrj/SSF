package com.jiaxy.ssf.message;

import io.netty.buffer.ByteBuf;

import java.io.Serializable;

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
public abstract class AbstractMessage implements Serializable{

    //-----------------Message Type----------------------
    public static final int REQUEST_MSG = 1;

    public static final int RESPONSE_MSG = 2;

    public static final int HEARTBEAT_REQUEST_MSG = 3;

    public static final int HEARTBEAT_RESPONSE_MSG = 4;

    public static final int CALLBACK_REQUEST_MSG = 5;

    public static final int CALLBACK_RESPONSE_MSG = 6;

    private MessageHead head;

    private ByteBuf msgBuf;

    private ByteBuf msgBodyBuf;


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


    public boolean isHeartBeat(){
        return isHeartBeatRequestMsg() || isHeartBeatResponseMsg();
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


    public byte getProtocolType(){
        return getHead().getProtocolType();
    }

    public byte getCodecType(){
        return getHead().getCodecType();
    }

    public MessageHead getHead() {
        return head;
    }

    public void setHead(MessageHead head) {
        this.head = head;

    }

    public ByteBuf getMsgBuf() {
        return msgBuf;
    }

    public void setMsgBuf(ByteBuf msgBuf) {
        this.msgBuf = msgBuf;
    }

    public ByteBuf getMsgBodyBuf() {
        return msgBodyBuf;
    }

    public void setMsgBodyBuf(ByteBuf msgBodyBuf) {
        this.msgBodyBuf = msgBodyBuf;
    }

    @Override
    public String toString() {
        return "{" +
                "head=" + head +
                '}';
    }
}
