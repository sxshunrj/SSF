package com.jiaxy.ssf.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/08 11:25
 */
public class ReflectUtil {


    public static Object invokeMethod(Method method,Object target,Object... args){
       try {
           return method.invoke(target,args);
       } catch (Exception e){
           handleReflectionException(e);
       }
       throw new IllegalStateException("Should never get here");
    }


    public static void rethrowRuntimeException(Throwable e){
        if (e instanceof RuntimeException){
            throw (RuntimeException)e;
        }
        if (e instanceof Error){
            throw (Error)e;
        }
        throw new RuntimeException(e);
    }

    private static void handleReflectionException(Exception e){
        if (e instanceof NoSuchMethodException){
            throw new IllegalStateException("Method not found: " + e.getMessage());
        } else if (e instanceof IllegalAccessException){
            throw new IllegalStateException("Could not access method: " + e.getMessage());
        } else if (e instanceof InvocationTargetException){
            rethrowRuntimeException(e.getCause());
        } else if (e instanceof RuntimeException){
            throw (RuntimeException)e;
        }
        throw new UndeclaredThrowableException(e);
    }

}
