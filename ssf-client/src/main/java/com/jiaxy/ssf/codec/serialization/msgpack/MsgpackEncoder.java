package com.jiaxy.ssf.codec.serialization.msgpack;

import com.jiaxy.ssf.codec.serialization.Encoder;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 18:17
 */
public class MsgpackEncoder implements Encoder {

    @Override
    public byte[] encode(Object obj) {
       return null;
    }

    @Override
    public byte[] encode(Object obj, String clzName) {
        return new byte[0];
    }
}
