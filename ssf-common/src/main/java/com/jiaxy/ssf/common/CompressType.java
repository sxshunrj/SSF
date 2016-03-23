package com.jiaxy.ssf.common;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 16:38
 */
public enum CompressType {

    NONE(Byte.valueOf("0")),

    LZMA(Byte.valueOf("1")),

    SNAPPY(Byte.valueOf("2"))

    ;

    private byte value;

    private CompressType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
