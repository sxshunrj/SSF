package com.jiaxy.ssf.message;

import com.jiaxy.ssf.common.CodecType;
import com.jiaxy.ssf.common.CompressType;
import com.jiaxy.ssf.common.ProtocolType;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 14:49
 */
public class MessageHead implements Serializable,Cloneable {

    //----------------HEAD ATTR CONSTANTS-------------
    //client call time out key
    public static final Byte CLIENT_CALL_TIMEOUT = (byte)1;


    private int fullLength;

    private short headLength;

    private byte protocolType = ProtocolType.SSF.getValue();

    private byte codecType = CodecType.JAVA.getValue();

    private int messageType;

    private byte compressType = CompressType.NONE.getValue();

    private int msgId;

    private ConcurrentHashMap<Byte,Object> attrMap = new ConcurrentHashMap<Byte, Object>();

    public int getFullLength() {
        return fullLength;
    }

    public void setFullLength(int fullLength) {
        this.fullLength = fullLength;
    }

    public short getHeadLength() {
        return headLength;
    }

    public void setHeadLength(short headLength) {
        this.headLength = headLength;
    }

    public byte getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(byte protocolType) {
        this.protocolType = protocolType;
    }

    public byte getCodecType() {
        return codecType;
    }

    public void setCodecType(byte codecType) {
        this.codecType = codecType;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public byte getCompressType() {
        return compressType;
    }

    public void setCompressType(byte compressType) {
        this.compressType = compressType;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public ConcurrentHashMap<Byte, Object> getAttrMap() {
        return attrMap;
    }

    public Object getAttrValue(Byte key){
        return attrMap.get(key);
    }

    @Override
    public MessageHead clone(){
        MessageHead clonedHead = new MessageHead();
        clonedHead.setFullLength(this.fullLength);
        clonedHead.setHeadLength(this.headLength);
        clonedHead.setCodecType(this.codecType);
        clonedHead.setCompressType(this.compressType);
        clonedHead.setMessageType(this.messageType);
        clonedHead.setMsgId(this.msgId);
        clonedHead.setProtocolType(this.protocolType);
        clonedHead.getAttrMap().putAll(this.attrMap);
        return clonedHead;
    }

    @Override
    public String toString() {
        return "MessageHead{" +
                "protocolType=" + protocolType +
                ", codecType=" + codecType +
                ", messageType=" + messageType +
                ", compressType=" + compressType +
                ", msgId=" + msgId +
                ", attrMap=" + attrMap +
                '}';
    }
}
