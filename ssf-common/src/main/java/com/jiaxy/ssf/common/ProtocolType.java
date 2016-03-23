package com.jiaxy.ssf.common;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/17 15:09
 */
public enum ProtocolType {

    SSF((byte)1);

    private byte value;

    private ProtocolType(byte value) {
        this.value = value;
    }



    public byte getValue() {
        return value;
    }
}
