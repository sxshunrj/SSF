package com.jiaxy.ssf.proxy;

import com.jiaxy.ssf.processor.CallbackProcessor;
import com.jiaxy.ssf.processor.MessageProcessor;
import com.jiaxy.ssf.service.Callback;

import java.util.concurrent.ConcurrentHashMap;

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


    private static final ConcurrentHashMap<String,MethodCallback> cacheProxyInstance = new ConcurrentHashMap<String, MethodCallback>();



    public static <T> T getProxy(ProxyType proxyType,Class<T> serviceInterfaceClz,MessageProcessor processor) {
        switch ( proxyType ){
            case JAVASSIT:
            case JDK:
                return JDKDynamicProxy.getProxy(serviceInterfaceClz,processor);
            default:
                return JDKDynamicProxy.getProxy(serviceInterfaceClz,processor);
        }
    }

    /**
     *
     * @param proxyType
     * @param serviceInterfaceName
     * @param methodName
     * @param processor
     * @return callback proxy instance
     */
    public static Callback getCallbackProxy(ProxyType proxyType,String serviceInterfaceName,String methodName,CallbackProcessor processor) {
        Class<Callback> serviceInterfaceClz = Callback.class;
        String key = serviceInterfaceName +":"+methodName;
        MethodCallback callback = cacheProxyInstance.get(key);
        if (callback == null){
            callback = new MethodCallback();
            MethodCallback old = cacheProxyInstance.putIfAbsent(key,callback);
            if (old != null){
                callback = old;
            }
        }
        if (callback.getCallback() == null){
            synchronized (callback){
                if (callback.getCallback() == null){
                    switch ( proxyType ){
                        case JAVASSIT:
                        case JDK:
                            callback.setCallback(JDKDynamicProxy.getProxy(serviceInterfaceClz,processor));
                        default:
                            callback.setCallback(JDKDynamicProxy.getProxy(serviceInterfaceClz,processor));
                    }
                }
            }
        }
        return callback.getCallback();
    }

    public static void removeCallbackProxy(String serviceInterfaceName,String methodName){
        String key = serviceInterfaceName +":"+methodName;
        cacheProxyInstance.remove(key);
    }


    static class MethodCallback{

        private Callback callback;


        public Callback getCallback() {
            return callback;
        }

        public void setCallback(Callback callback) {
            this.callback = callback;
        }
    }
}
