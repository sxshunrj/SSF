package com.jiaxy.ssf.message;

import io.netty.buffer.ByteBuf;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 14:48
 */
public abstract class AbstractMessage {

    private MessageHead head;

    private ByteBuf msg;

    private ByteBuf msgBody;


    protected AbstractMessage(boolean initMessageHead){
        if ( initMessageHead ){
            head = new MessageHead();
        }
    }

    public MessageHead getHead() {
        return head;
    }

    public void setHead(MessageHead head) {
        this.head = head;
    }

    public ByteBuf getMsg() {
        return msg;
    }

    public void setMsg(ByteBuf msg) {
        this.msg = msg;
    }

    public ByteBuf getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(ByteBuf msgBody) {
        this.msgBody = msgBody;
    }
}
