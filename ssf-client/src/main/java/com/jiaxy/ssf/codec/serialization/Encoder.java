package com.jiaxy.ssf.codec.serialization;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 14:42
 */
public interface Encoder {


    public byte[] encode(Object obj);


    public byte[] encode(Object obj,String clzName);




}
