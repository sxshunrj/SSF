package com.jiaxy.ssf.codec.serialization.java;

import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.RequestMessageBody;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class JavaEncoderTest {

    @Test
    public void encodeTest() throws Exception {
        RequestMessage requestMessage = new RequestMessage(true);
        RequestMessageBody requestMessageBody = new RequestMessageBody();
        requestMessageBody.setAlias("test");
        requestMessageBody.setArgs(new Object[]{"args", new ArrayList<Object>()});
        requestMessageBody.setArgsType(new String[]{"java.lang.String","java.util.List"});
        requestMessage.setRequestMessageBody(requestMessageBody);
        requestMessage.getHead().getAttrMap().put(Byte.valueOf("1"), "SSF JAVA ENCODE");
        JavaEncoder encoder = new JavaEncoder();
        byte[] bytes = encoder.encode(requestMessage);
        JavaDecoder decoder = new JavaDecoder();
        RequestMessage requestMessageDecoded = (RequestMessage) decoder.decode(bytes,RequestMessage.class);
        Assert.assertEquals("test",requestMessageDecoded.getRequestMessageBody().getAlias());
        Assert.assertEquals(new String[]{"java.lang.String","java.util.List"}[1],requestMessageDecoded.getRequestMessageBody().getArgsType()[1]);

    }
}