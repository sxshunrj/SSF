package com.jiaxy.ssf.util;

import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/06 18:45
 */
public class ResponseUtil {

    private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);


    public static void sendExceptionMessage(final Throwable throwable,AbstractMessage source,Channel channel){
        ResponseMessage responseMessage = new ResponseMessage(false);
        responseMessage.setHead(source.getHead().clone());
        responseMessage.setException(throwable);
        ChannelFuture channelFuture = channel.writeAndFlush(responseMessage);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if ( !future.isSuccess() ){
                    logger.error("send error message to remote error.Cause by:",throwable);
                }
            }
        });
    }
}
