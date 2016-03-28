package com.jiaxy.ssf.codec.protocol.ssf;

import com.jiaxy.ssf.codec.protocol.Protocol;
import com.jiaxy.ssf.codec.serialization.Decoder;
import com.jiaxy.ssf.codec.serialization.Encoder;
import com.jiaxy.ssf.common.Constants;
import com.jiaxy.ssf.common.DataTypeUtil;
import com.jiaxy.ssf.exception.SSFCodecException;
import com.jiaxy.ssf.message.MessageBody;
import com.jiaxy.ssf.message.MessageHead;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     SSF Protocol
 * </p>
 * <pre>
 *+----------------------------------------------------------------------------------------------------------------------+
 *|                                                  MAGIC CODE(1B):COLE                                                 |
 *+----------+-----------------------------------------------------------------------------------------------------------+
 *|          |                                       FULL LENGTH:HEAD + BODY -MAGIC                                      |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                              HEAD LENGTH:Exclude Full Length and HEAD Length                              |
 *|          +-----------------------------------------------------+-----------------------------------------------------+
 *|          |                  PROTOCOL TYPE(1B)                  |                    CODEC TYPE(1B)                   |
 *|          +-----------------------------------------------------+-----------------------------------------------------+
 *|          |                   MESSAGE TYPE(1B)                  |                  COMPRESS TYPE(1B)                  |
 *|          +-----------------------------------------------------+-----------------------------------------------------+
 *|          |                                                 MESGID(4B)                                                |
 *|          +--------------------+--------------------------------------------------------------------------------------+
 *|HEAD      |                    |                                     MAP SIZE(1B)                                     |
 *|          |                    +--------------------------------------------------------------------------------------+
 *|          |                    |                                      MAP KEY(1B)                                     |
 *|          |                    +--------------------------------------------------------------------------------------+
 *|          |                    |                      ATTR TYPE(1B) 1:int,2:string,3:byte,4:short                     |
 *|          |    [OPT]ATTRMAP    +--------------------------------------------------------------------------------------+
 *|          |                    |                                       ATTR VAL:                                      |
 *|          |                    |                                       ?B int:4B                                      |
 *|          |                    |                            string:length(2B) +data length                            |
 *|          |                    |                                   byte:2B,short:2B                                   |
 *+----------+--------------------+--------------------------------------------------------------------------------------+
 *|          |                                             className[String]                                             |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                                               alias[String]                                               |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                                             methodName[String]                                            |
 *|BODY      +-----------------------------------------------------------------------------------------------------------+
 *|          |                                             argsType[String[]]                                            |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                                               args[Object[]]                                              |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                                              attachments[Map]                                             |
 *+----------+-----------------------------------------------------------------------------------------------------------+
 * </pre>
 *
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 11:58
 */
public class SSFProtocol implements Protocol {

    private static final Logger logger = LoggerFactory.getLogger(SSFProtocol.class);

    private Encoder encoder;

    private Decoder decoder;

    public SSFProtocol(Encoder encoder, Decoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public Object decode(ByteBuf data, Class clz) {
        byte[] dataArr = new byte[data.readableBytes()];
        data.readBytes(dataArr);
        return decoder.decode(dataArr,clz);
    }

    @Override
    public Object decode(ByteBuf data, String clzName) {
        byte[] dataArr = new byte[data.readableBytes()];
        data.readBytes(dataArr);
        return decoder.decode(dataArr,clzName);
    }

    @Override
    public ByteBuf encode(Object obj, ByteBuf buf) {
        if ( obj instanceof RequestMessage ){
            RequestMessage requestMessage = (RequestMessage) obj;
            MessageHead messageHead = requestMessage.getHead();
            MessageBody messageBody = requestMessage.getMessageBody();
            messageHead.setHeadLength(encodeMessageHead(messageHead,buf));
            if ( messageBody != null ){
                byte[] messageBodyByteArr = encoder.encode(messageBody);
                //TODO compress feature
                buf = buf.writeBytes(messageBodyByteArr);
                messageHead.setFullLength(buf.readableBytes());
            }
        } else if ( obj instanceof ResponseMessage ){
            ResponseMessage responseMessage = (ResponseMessage) obj;
            MessageHead messageHead = responseMessage.getHead();
            byte[] responseMsgByteArr = encoder.encode(responseMessage);
            //TODO compress feature
            messageHead.setHeadLength(encodeMessageHead(messageHead,buf));
            buf = buf.writeBytes(responseMsgByteArr);
            messageHead.setFullLength(buf.readableBytes());
        } else {
            logger.error(" unknown message:{}",obj != null ? obj.getClass().getCanonicalName() :" message is null.");
            throw new SSFCodecException("unknown message");
        }
        return buf;
    }

    @Override
    public short encodeMessageHead(MessageHead head, ByteBuf buf) {
        //if attr map size is 0
        short headLength = 8;
        if ( buf.capacity() < 8 ){
            buf.capacity(8);
        }
        int headLengthIndex = buf.writerIndex();
        //see protocol structure
        buf.writeShort(headLength);
        buf.writeByte(head.getProtocolType());
        buf.writeByte(head.getCodecType());
        buf.writeByte(head.getMessageType());
        buf.writeByte(head.getCompressType());
        buf.writeInt(head.getMsgId());
        if ( head.getAttrMap().size() > 0 ){
            headLength += encodeHeadAttrMap(head.getAttrMap(),buf);
            buf.setBytes(headLengthIndex, DataTypeUtil.convertShort2Bytes(headLength));
        }
        return headLength;
    }

    @Override
    public MessageHead decodeMessageHead(ByteBuf buf, int headLength) {
        byte protocolType = buf.readByte();
        byte codecType = buf.readByte();
        byte msgType = buf.readByte();
        byte compressType = buf.readByte();
        int msgId = buf.readInt();
        MessageHead messageHead = new MessageHead();
        messageHead.setProtocolType(protocolType);
        messageHead.setCodecType(codecType);
        messageHead.setMessageType(msgType);
        messageHead.setCompressType(compressType);
        messageHead.setMsgId(msgId);
        // attr map is not empty
        if ( headLength > 8 ){
            decodeHeadAttrMap(messageHead.getAttrMap(),buf);
        }
        return messageHead;
    }



    private short encodeHeadAttrMap(Map<Byte,Object> attrMap,ByteBuf buf){
        buf.writeByte(attrMap.size());
        short length = 1;
        for ( Map.Entry<Byte,Object> entry : attrMap.entrySet() ){
            Byte key = entry.getKey();
            Object value = entry.getValue();
            if ( value instanceof Integer ){
                buf.writeByte(key);
                buf.writeByte((byte)1);//value type .see protocol
                buf.writeInt((Integer) value);
                length += 6;
            } else if ( value instanceof String ){
                buf.writeByte(key);
                buf.writeByte((byte)2);
                byte[] vs = ((String) value).getBytes(Constants.DEFAULT_CHARSET);
                buf.writeShort(vs.length);
                buf.writeBytes(vs);
                length += ( 4 + vs.length );
            } else if ( value instanceof Byte ){
                buf.writeByte(key);
                buf.writeByte((byte)3);
                buf.writeByte((Byte)value);
                length += 3;
            } else if ( value instanceof Short ){
                buf.writeByte(key);
                buf.writeByte((byte)4);
                buf.writeShort((Short) value);
                length += 4;
            } else {
                throw new SSFCodecException("value of attr map in message head must be byte or short or int or string");
            }
        }
        return length;
    }

    private void decodeHeadAttrMap(Map<Byte,Object> attrMap,ByteBuf buf){
        byte size = buf.readByte();
        for ( int i = 0 ;i < size ;i++ ){
            byte key = buf.readByte();
            Object value = null;
            byte valueType = buf.readByte();
            switch ( valueType ){
                case 1://int
                    value = buf.readInt();
                    attrMap.put(key,value);
                    break;
                case 2://string
                    byte strLength = buf.readByte();
                    byte[] strBytes = new byte[strLength];
                    buf.readBytes(strBytes);
                    attrMap.put(key,new String(strBytes,Constants.DEFAULT_CHARSET));
                    break;
                case 3://byte
                    value = buf.readByte();
                    attrMap.put(key,value);
                    break;
                case 4://short
                    value = buf.readShort();
                    attrMap.put(key,value);
                    break;
                default:
                    throw new SSFCodecException("value of attr map in message head must be byte or short or int or string");
            }
        }
    }


}
