package com.jiaxy.ssf.proxy;

import com.jiaxy.ssf.common.ClassUtil;
import com.jiaxy.ssf.processor.MessageProcessor;

import java.lang.reflect.Proxy;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/15 17:12
 */
public class JDKDynamicProxy {


    public static <T> T getProxy(Class<T> serviceInterfaceClz,MessageProcessor processor) {
        return (T) Proxy.newProxyInstance(ClassUtil.getDefaultClassLoader(),
                new Class[]{serviceInterfaceClz},
                new ProcessorInvocationHandler(processor));
    }
}
