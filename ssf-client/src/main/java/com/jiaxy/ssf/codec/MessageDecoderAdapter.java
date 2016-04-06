package com.jiaxy.ssf.codec;

import com.jiaxy.ssf.codec.protocol.ssf.ByteToSSFMessageDecoder;
import com.jiaxy.ssf.codec.protocol.ssf.SSFMessageToByteEncoder;
import com.jiaxy.ssf.codec.protocol.ssf.SSFProtocol;
import com.jiaxy.ssf.config.ServerTransportConfig;
import com.jiaxy.ssf.transport.server.ServerChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/05 13:29
 */
public class MessageDecoderAdapter extends ByteToMessageDecoder{


    private ServerChannelHandler serverChannelHandler;

    private ServerTransportConfig serverTransportConfig;


    public MessageDecoderAdapter(ServerChannelHandler serverChannelHandler, ServerTransportConfig serverTransportConfig) {
        this.serverChannelHandler = serverChannelHandler;
        this.serverTransportConfig = serverTransportConfig;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if ( in.readableBytes() < 2 ){
            return;
        }
        Short magicCodePart1 = in.getUnsignedByte(0);
        Short magicCodePart2 = in.getUnsignedByte(1);
        if ( isSSF(magicCodePart1.byteValue(),magicCodePart2.byteValue()) ){
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.addLast(new ByteToSSFMessageDecoder(serverTransportConfig.getPayload()))
                    .addLast(new SSFMessageToByteEncoder())
                    .addLast(serverChannelHandler)
                    .remove(this);
            pipeline.fireChannelActive();
        }

    }


    private boolean isSSF(byte magicCodePart1,byte magicCodePart2){
        if ( magicCodePart1 == SSFProtocol.MAGIC_CODE_BYTES[0]
                && magicCodePart2 == SSFProtocol.MAGIC_CODE_BYTES[1] ){
            return true;
        } else {
            return false;
        }
    }
}
