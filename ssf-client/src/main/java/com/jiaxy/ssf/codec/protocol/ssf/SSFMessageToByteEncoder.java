package com.jiaxy.ssf.codec.protocol.ssf;

import com.jiaxy.ssf.codec.protocol.Protocol;
import com.jiaxy.ssf.codec.protocol.ProtocolFactory;
import com.jiaxy.ssf.exception.SSFCodecException;
import com.jiaxy.ssf.message.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 18:36
 */
public class SSFMessageToByteEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if ( out == null ){
            out = ctx.alloc().buffer();
        }
        if ( msg instanceof AbstractMessage ){
            AbstractMessage message = (AbstractMessage) msg;
            if ( message.getMsgBuf() != null ){
                encodeProtocolDecorator(message.getMsgBuf(),out);
                message.getMsgBuf().release();
            } else {
                ByteBuf buf = ctx.alloc().heapBuffer();
                Protocol protocol = ProtocolFactory.getProtocol(message.getHead().getProtocolType(),
                        message.getHead().getCodecType());
                protocol.encode(msg,buf);
                encodeProtocolDecorator(buf, out);
                buf.release();
            }
        } else {
            throw new SSFCodecException("unknown message.");
        }
    }

    /**
     * add protocol info into
     *
     * @param buf
     */
    private void encodeProtocolDecorator(ByteBuf buf,ByteBuf out){
        /**
         * 2B:magic code
         * 4B:length of full length field
         */
        int dataTotalLength = 2 + 4 + buf.readableBytes();
        if ( out.capacity() < dataTotalLength ){
            out.capacity(dataTotalLength);
        }
        out.writeBytes(SSFProtocol.MAGIC_CODE_BYTES);
        //full length
        out.writeInt(dataTotalLength - 2);
        out.writeBytes(buf,buf.readerIndex(),buf.readableBytes());
    }

}
