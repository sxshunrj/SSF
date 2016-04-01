package com.jiaxy.ssf.codec.serialization.java;

import com.jiaxy.ssf.codec.serialization.Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 18:12
 */
public class JavaDecoder implements Decoder {

    private static final Logger logger = LoggerFactory.getLogger(JavaDecoder.class);

    @Override
    public Object decode(byte[] data, Class clz) {
        return decode(data,clz.getCanonicalName());
    }

    @Override
    public Object decode(byte[] data, String clzName) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bis);
            return  readObject(ois);
        } catch ( Exception e ){
            logger.error("decode {} data error",clzName,e);
        } finally {
            if ( ois != null ){
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }


    private Object readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        byte b = ois.readByte();
        if ( b == 0 ){
            return null;
        } else {
            return ois.readObject();
        }
    }
}
