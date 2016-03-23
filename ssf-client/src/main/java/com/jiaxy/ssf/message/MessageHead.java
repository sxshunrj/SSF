package com.jiaxy.ssf.message;

import com.jiaxy.ssf.common.CodecType;
import com.jiaxy.ssf.common.CompressType;
import com.jiaxy.ssf.common.ProtocolType;

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
public class MessageHead {

    private int fullLength;

    private short headLength;

    private byte protocolType = ProtocolType.SSF.getValue();

    private byte codecType = CodecType.JAVA.getValue();

    private int messageType;

    private byte compressType = CompressType.NONE.getValue();

    private int msgId;

    private ConcurrentHashMap<String,Object> attrMap = new ConcurrentHashMap<String, Object>();




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

    public ConcurrentHashMap<String, Object> getAttrMap() {
        return attrMap;
    }

    public void setAttrMap(ConcurrentHashMap<String, Object> attrMap) {
        this.attrMap = attrMap;
    }


}
