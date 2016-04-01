package com.jiaxy.ssf.common;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 16:32
 */
public enum CodecType {

    JAVA((byte)1),

    HESSIAN((byte)2),

    MSGPACK((byte)3),

    JSON((byte)4)

    ;


    private byte value;

    private CodecType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static CodecType valueOf(byte value){
        for ( CodecType codecType : CodecType.values() ){
            if ( codecType.getValue() == value ){
                return codecType;
            }
        }
        return null;
    }
}
