package com.jiaxy.ssf.codec.protocol;

import com.jiaxy.ssf.codec.protocol.ssf.SSFProtocol;
import com.jiaxy.ssf.codec.serialization.Decoder;
import com.jiaxy.ssf.codec.serialization.Encoder;
import com.jiaxy.ssf.codec.serialization.hessian.HessianDecoder;
import com.jiaxy.ssf.codec.serialization.hessian.HessianEncoder;
import com.jiaxy.ssf.codec.serialization.java.JavaDecoder;
import com.jiaxy.ssf.codec.serialization.java.JavaEncoder;
import com.jiaxy.ssf.codec.serialization.json.JSONEncoder;
import com.jiaxy.ssf.codec.serialization.msgpack.MsgpackDecoder;
import com.jiaxy.ssf.codec.serialization.msgpack.MsgpackEncoder;
import com.jiaxy.ssf.common.CodecType;
import com.jiaxy.ssf.common.ProtocolType;
import com.jiaxy.ssf.exception.InitException;

import java.util.concurrent.ConcurrentHashMap;

import static com.jiaxy.ssf.common.ProtocolType.*;
import static com.jiaxy.ssf.common.CodecType.*;
/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     Protocol Factory
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/28 18:00
 */
public class ProtocolFactory {

    private static final ConcurrentHashMap<String,Protocol> protocolMap = new ConcurrentHashMap<String, Protocol>();

    static {
        protocolMap.put(createProtocolKey(SSF,HESSIAN),initProtocol(SSF,HESSIAN));
    }

    public static Protocol getProtocol(ProtocolType protocolType,CodecType codecType){
        String key = createProtocolKey(protocolType,codecType);
        Protocol protocol = protocolMap.get(key);
        if ( protocol == null ){
            synchronized (ProtocolFactory.class){
                if ( protocolMap.get(key) == null ){
                    protocol = initProtocol(protocolType,codecType);
                    protocolMap.put(key,protocol);
                }
            }
        }
        return protocol;
    }

    private static Protocol initProtocol(ProtocolType protocolType,CodecType codecType){
        Encoder encoder;
        Decoder decoder;
        Protocol protocol;
        switch ( codecType ){
            case JAVA:
                encoder = new JavaEncoder();
                decoder = new JavaDecoder();
                break;
            case JSON:
                encoder = new JSONEncoder();
                decoder = new JavaDecoder();
                break;
            case HESSIAN:
                encoder = new HessianEncoder();
                decoder = new HessianDecoder();
                break;
            case MSGPACK:
                encoder = new MsgpackEncoder();
                decoder = new MsgpackDecoder();
                break;
            default:
                throw new InitException("can't init protocol for unknown codec type:"+codecType);

        }
        switch ( protocolType ){
            case SSF:
                protocol = new SSFProtocol(encoder,decoder);
                break;
            default:
                throw new InitException("can't init protocol for unknown protocol type:"+protocolType);
        }
        return protocol;
    }


    private static String createProtocolKey(ProtocolType protocolType,CodecType codecType){
       return protocolType.name()+"#"+codecType.name();
    }

}
