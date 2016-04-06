package com.jiaxy.ssf.codec.protocol.ssf;

import com.jiaxy.ssf.codec.protocol.Protocol;
import com.jiaxy.ssf.codec.protocol.ProtocolFactory;
import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.exception.RpcException;
import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.MessageHead;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @see com.jiaxy.ssf.codec.protocol.ssf.SSFMessageToByteEncoder
 *
 * @since 2016/03/28 18:37
 */
public class ByteToSSFMessageDecoder extends LengthFieldBasedFrameDecoder{


    public ByteToSSFMessageDecoder(int maxFrameLength){
        /**
         * 2B magic code
         * 4B lengthFieldLength
         * -4B lengthAdjustment for the length of the Full length field
         * 6B initialBytesToStrip.strip magic + full length field
         */
       super(maxFrameLength,2,4,-4,6);

    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object frame = super.decode(ctx, in);
        if ( frame == null ){
            return null;
        }
        return decodeSSFFrame((ByteBuf) frame);
    }

    protected Object decodeSSFFrame(ByteBuf frame){

        MessageHead messageHead = null;
        /**
         * 2B: Magic Code
         * 4B: the length of the Full length field
         */
        int dataTotalLength = frame.readableBytes() + 2 + 4;
        Short headLength = frame.readShort();
        AbstractMessage message;
        Protocol protocol = ProtocolFactory.getProtocol(ProtocolType.SSF);
        try {
            messageHead = protocol.decodeMessageHead(frame,headLength);
            messageHead.setHeadLength(headLength);
            //TODO if compressed,need decompress
            message = decodeSSFMessageBody(frame,messageHead);
            messageHead.setFullLength( dataTotalLength - 2);
        } catch ( Exception e ){
            throw RpcException.convertToRpcException(e);
        }
        return message;
    }

    private AbstractMessage decodeSSFMessageBody(ByteBuf buf,MessageHead head){
        AbstractMessage message = null;
        try {
            switch ( head.getMessageType() ){

                case AbstractMessage.REQUEST_MSG:
                    RequestMessage requestMessage = new RequestMessage(false);
                    requestMessage.setReceivedTime(System.currentTimeMillis());
                    requestMessage.setMsgBodyBuf(buf.slice(buf.readerIndex(), buf.readableBytes()));
                    message = requestMessage;
                    break;
                case AbstractMessage.RESPONSE_MSG:
                    ResponseMessage responseMessage = new ResponseMessage(false);
                    responseMessage.setMsgBodyBuf(buf.slice(buf.readerIndex(), buf.readableBytes()));
                    message = responseMessage;
                    break;
                case AbstractMessage.HEARTBEAT_REQUEST_MSG:
                    message = new RequestMessage(false);
                    break;
                case AbstractMessage.HEARTBEAT_RESPONSE_MSG:
                    message = new ResponseMessage(false);
                    break;
                case AbstractMessage.CALLBACK_RESPONSE_MSG:
                    break;
                case AbstractMessage.CALLBACK_REQUEST_MSG:
                    break;
                default:
                    throw new RpcException("unknown message type:{}"+head.getMessageType());
            }
            if ( message != null ){
                message.setHead(head);
            }
        } catch ( Exception e ){
            throw RpcException.convertToRpcException(e);
        }
        return message;
    }
}
