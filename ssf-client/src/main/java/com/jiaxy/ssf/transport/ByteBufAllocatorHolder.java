package com.jiaxy.ssf.transport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/29 18:14
 */
public class ByteBufAllocatorHolder {


    private static ByteBufAllocator byteBufAllocator = new UnpooledByteBufAllocator(false);

    public static ByteBuf getBuf(){
        return byteBufAllocator.buffer();
    }


    public static ByteBuf getBuf(int initialCapacity){
        return byteBufAllocator.buffer(initialCapacity);
    }
}
