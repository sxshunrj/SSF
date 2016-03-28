package com.jiaxy.ssf.codec.serialization;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 14:53
 */
public interface Decoder {

    public Object decode(byte[] data,Class clz);

    public Object decode(byte[] data,String clzName);

}
