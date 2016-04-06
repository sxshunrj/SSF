package com.jiaxy.ssf.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/25 13:25
 */
public class NetUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public static String channelToString(SocketAddress local,SocketAddress remote){
        StringBuilder channelStr = new StringBuilder();
        if ( local instanceof InetSocketAddress ) {
            channelStr.append(ipPortString((InetSocketAddress) local));
        } else {
            channelStr.append(local);
        }
        if ( remote instanceof InetSocketAddress ) {
            channelStr.append("->");
            channelStr.append(ipPortString((InetSocketAddress) remote));
        } else {
            channelStr.append("->");
            channelStr.append(remote);
        }
        return channelStr.toString();
    }

    public static String channelToString( Channel channel ){
        return channelToString(channel.localAddress(),channel.remoteAddress());
    }

    public static String ipString(InetSocketAddress address){
        if ( address == null ){
            return "";
        }
        return address.getAddress() == null ? address.getHostName() : address.getAddress().getHostAddress();
    }

    public static String ipPortString(InetSocketAddress address){
        if ( address == null ){
            return "";
        }
        return ipString(address) +":"+address.getPort();
    }


    public static void writeAndFlush(final Channel channel, Object object, final boolean showAsError){
        ChannelFuture channelFuture = channel.writeAndFlush(object);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if ( !future.isSuccess() && showAsError ){
                    logger.error(" write and flush error:{}",channelToString(channel.localAddress(),channel.remoteAddress()),future.cause());
                } else {
                    logger.warn(" write and flush error:{}", channelToString(channel.localAddress(), channel.remoteAddress()));
                }
            }
        });
    }
}
