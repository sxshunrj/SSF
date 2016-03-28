package com.jiaxy.ssf.codec.protocol;

import com.jiaxy.ssf.message.MessageHead;
import io.netty.buffer.ByteBuf;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     dismantle specific protocol
 *
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 11:48
 */
public interface Protocol {


    Object decode(ByteBuf data,Class clz);

    Object decode(ByteBuf data,String clzName);

    ByteBuf encode(Object obj,ByteBuf buf);

    /**
     *
     * @param head message head
     * @param buf
     * @return the length of head
     */
    short encodeMessageHead(MessageHead head,ByteBuf buf);


    /**
     *
     * @param buf
     * @param headLength head length
     * @return message head
     */
    MessageHead decodeMessageHead(ByteBuf buf,int headLength);

}
