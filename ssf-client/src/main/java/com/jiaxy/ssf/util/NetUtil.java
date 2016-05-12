package com.jiaxy.ssf.util;

import com.jiaxy.ssf.common.Constants;
import com.jiaxy.ssf.common.StringUtil;
import com.jiaxy.ssf.exception.InitException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Pattern;

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

    /**
     * all IPv4 addresses on the local machine
     */
    public static final String ALL_ADDRESS = "0.0.0.0";

    public static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    public static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    public static final int MIN_PORT = 0;

    public static final int MAX_PORT = 65535;

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

    public static int port(InetSocketAddress address){
        if ( address == null ){
            return -1;
        }
        return address.getPort();
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

    public static InetAddress getLocalAddress(){
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
            if ( isValidAddress(address)){
                return address;
            }
        } catch (UnknownHostException e) {
            logger.warn("get local ip error:{}",e.getMessage());
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if ( interfaces != null ){
                while (interfaces.hasMoreElements()){
                    NetworkInterface networkInterface = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    if (addresses != null){
                        while (addresses.hasMoreElements()){
                            address = addresses.nextElement();
                            if ( isValidAddress(address)){
                                return address;
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("get local ip error:{}", e.getMessage());
        }
        return address;
    }

    /**
     * get available port by try
     *
     * @param host
     * @param port
     * @return
     */
    public static int tryAvailablePort(String host,int port){
        if (!isAllAddress(host)
                && !isLocalHost(host)
                && !isNetworkCardHost(host)) {
            throw new InitException(String.format("%s is not found in network card,check it.", host));
        }
        if (port < MIN_PORT) {
            port = Constants.DEFAULT_SERVER_PORT;
        }
        for (int i = port; i <= MAX_PORT; i++) {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket();
                ss.bind(new InetSocketAddress(host, i));
                return i;
            } catch (Throwable e) {
                logger.warn("Bound to [{}:{}] failed",
                        host,
                        i);
                logger.info("Try next port:{}", i + 1);
            } finally {
                if (ss != null) {
                    try {
                        ss.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        throw new InitException(String.format("Can't bind to any port of %s,check port config:%d",host,port));
    }


    public static boolean isValidAddress(InetAddress address){
        if ( address == null || address.isLoopbackAddress() ){
            return false;
        }
        String ip = address.getHostAddress();
        if ( !ALL_ADDRESS.equals(ip)
                && !isLocalHost(ip)
                && !isAllAddress(ip)
                && isIPv4Address(ip)
                ){
            return true;
        }
        return false;

    }


    public static boolean isIPv4Address(String ip){
        return !StringUtil.isEmpty(ip) && IPV4_PATTERN.matcher(ip).matches();
    }

    public static boolean isLocalHost(String host) {
        return !StringUtil.isEmpty(host)
                && (LOCAL_IP_PATTERN.matcher(host).matches() || "localhost".equalsIgnoreCase(host));
    }


    public static boolean isAllAddress(String ip){
        return ALL_ADDRESS.equals(ip);
    }

    public static boolean isNetworkCardHost(String host){
        try {
            InetAddress address = InetAddress.getByName(host);
            return NetworkInterface.getByInetAddress(address) != null;
        } catch (Exception e) {
            return false;
        }
    }


    public static InetAddress tryActualHost(SocketAddress remote){
        Socket socket = new Socket();
        try {
            socket.connect(remote,1000);
            return socket.getLocalAddress();
        } catch (IOException e) {
            logger.warn("try connecting %s to get actual local host failed,%s",
                remote.toString(),
                    e.getMessage()
            );
        } finally {
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
        return null;
    }


    public static String tryActualHostByRegistryAddress(String registry){
        //TODO try connecting registry address
        return getLocalHost();
    }


    public static String getLocalHost(){
        InetAddress address = getLocalAddress();
        return address == null ? null :address.getHostAddress();
    }
}
