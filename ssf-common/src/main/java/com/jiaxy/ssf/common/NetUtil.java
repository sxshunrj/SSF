package com.jiaxy.ssf.common;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Channels;

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

}
