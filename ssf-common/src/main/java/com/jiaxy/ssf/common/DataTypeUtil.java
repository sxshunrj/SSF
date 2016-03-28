package com.jiaxy.ssf.common;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 17:42
 */
public class DataTypeUtil {

    public static byte[] convertShort2Bytes(short value){
        return new byte[]{
                (byte)(value >>> 8),
                (byte) value
        };
    }
}
