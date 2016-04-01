package com.jiaxy.ssf.codec.serialization.java;

import com.jiaxy.ssf.codec.serialization.Encoder;
import com.jiaxy.ssf.message.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
public class JavaEncoder implements Encoder {

    private static final Logger logger = LoggerFactory.getLogger(JavaEncoder.class);

    @Override
    public byte[] encode(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            if ( obj instanceof RequestMessage ){
                encodeRequestMessage((RequestMessage) obj,oos);
            }
            return bos.toByteArray();

        } catch (Exception e){
            logger.error("encode error",e);
        } finally {
            if ( oos != null ){
                try {
                    oos.close();
                } catch (IOException e) {
                    logger.warn("close object output stream error",e);
                }
            }
        }
        return new byte[0];
    }

    @Override
    public byte[] encode(Object obj, String clzName) {
        return encode(obj);
    }


    private void encodeRequestMessage(RequestMessage requestMessage,ObjectOutputStream oos) throws IOException {
        if ( requestMessage.isHeartBeat() ){
            oos.writeObject(null);
        } else {
            /*RequestMessageBody requestMessageBody = requestMessage.getRequestMessageBody();
            MessageHead messageHead = requestMessage.getHead();*/
            writeObject(requestMessage,oos);

        }

    }


    private void writeUTF(String value,ObjectOutputStream oos) throws IOException {
        if ( value == null ){
            oos.writeInt(-1);
        } else {
            oos.writeInt(value.length());
            oos.writeUTF(value);
        }
    }

    private void writeObject(Object value,ObjectOutputStream oos ) throws IOException {
        if ( value == null ){
            oos.writeByte(0);
        } else {
            oos.writeByte(1);
            oos.writeObject(value);
        }
    }
}
