package com.jiaxy.ssf.proxy;

import com.jiaxy.ssf.processor.MessageProcessor;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/15 16:43
 */
public class ServiceProxyFactory {




    public static <T> T getProxy(ProxyType proxyType,Class<T> serviceInterfaceClz,MessageProcessor processor) {
        switch ( proxyType ){
            case JAVASSIT:
            case JDK:
                return JDKDynamicProxy.getProxy(serviceInterfaceClz,processor);
            default:
                return JDKDynamicProxy.getProxy(serviceInterfaceClz,processor);
        }
    }


}
