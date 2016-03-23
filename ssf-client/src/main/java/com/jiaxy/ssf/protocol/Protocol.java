package com.jiaxy.ssf.protocol;

import io.netty.buffer.ByteBuf;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 09:48
 */
public interface Protocol {

    Object decode(ByteBuf data,Class clz);

    Object decode(ByteBuf data,String clzName);

    ByteBuf encode(Object msg,ByteBuf buf);
}
