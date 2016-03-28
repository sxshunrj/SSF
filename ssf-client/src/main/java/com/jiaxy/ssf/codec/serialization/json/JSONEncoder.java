package com.jiaxy.ssf.codec.serialization.json;

import com.jiaxy.ssf.codec.serialization.Encoder;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 18:13
 */
public class JSONEncoder implements Encoder {

    @Override
    public byte[] encode(Object obj) {
        return new byte[0];
    }

    @Override
    public byte[] encode(Object obj, String clzName) {
        return new byte[0];
    }
}
