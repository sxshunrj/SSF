package com.jiaxy.ssf.util;

import com.jiaxy.ssf.exception.InitException;
import com.jiaxy.ssf.service.Callback;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     cache callback some information and util method
 *
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/11 16:54
 */
public class CallbackInfo {

    private static final ConcurrentHashMap<String,Class> callbackParamType = new ConcurrentHashMap<String, Class>();

    private static final ConcurrentHashMap<String,Callback> callbackInstanceCache = new ConcurrentHashMap<String, Callback>();

    public static boolean isCallbackMethod(String serviceInterfaceName,String method){
        if (callbackParamType.contains(callbackRegistryKey(serviceInterfaceName,method))){
            return true;
        }
        return false;
    }

    public static boolean isCallback(Class clz){
        if (clz == Callback.class){
            return true;
        }
        Class[] clzArr = clz.getInterfaces();
        for (Class c:clzArr){
            if (c == Callback.class){
                return true;
            }
        }
        return false;
    }

    /**
     * if the service has some method witch callback param.
     *
     * cache some information for use
     *
     * @param serviceInterface
     */
    public static void callbackInfoRegister(Class serviceInterface){
        String serviceInterfaceName = serviceInterface.getCanonicalName();
        Method[] serviceMethods = serviceInterface.getDeclaredMethods();
        for (Method serviceMethod:serviceMethods){
            if (isCallbackMethod(serviceMethod)){
                Class ipoClz = null;
                Class opoClz = null;
                Type[] paramsTypes = serviceMethod.getGenericParameterTypes();
                for (Type paramsType:paramsTypes){
                    if (paramsType instanceof ParameterizedType){
                        ParameterizedType parameterizedType = (ParameterizedType) paramsType;
                        if (parameterizedType.getRawType() instanceof Class
                                && isCallback((Class) parameterizedType.getRawType())){
                            Type[] actualTypes = parameterizedType.getActualTypeArguments();
                            if (actualTypes.length == 2){
                                ipoClz = cast2Class(actualTypes[0]);
                                opoClz = cast2Class(actualTypes[1]);
                                break;
                            }
                        }
                    }
                }
                if (ipoClz == null){
                    throw new InitException(String.format("service:%s,callback method:%s callback param is invalid.check it.",
                            serviceInterfaceName,
                            serviceMethod.getName()));
                }
                callbackInfoRegister(serviceInterfaceName, serviceMethod.getName(), ipoClz);
            }
        }
    }

    public static void callbackInstanceRegister(String callbackInstanceId,Callback callback){
        callbackInstanceCache.putIfAbsent(callbackInstanceId,callback);
    }

    public static Callback getCallbackInstance(String callbackInstanceId){
        return callbackInstanceCache.get(callbackInstanceId);
    }

    public static String buildCallbackInstanceId(String host,int pid,Callback callback){
        Class callbackImpl = callback.getClass();
        String callbackName = callbackImpl.getCanonicalName() != null ? callbackImpl.getCanonicalName() : callbackImpl.getName();
        return host+"_"+pid+"_"+callbackName;
    }


    private static Class cast2Class(Type type){
        try {
            if (type instanceof Class){
                return (Class) type;
            } else {
                return (Class) ((ParameterizedType) type).getRawType();
            }
        } catch (Exception e){
            throw new InitException("callback parameterized type must be actual type.");
        }
    }

    private static void callbackInfoRegister(String serviceInterfaceName,String method,Class paramClz){
        String key = callbackRegistryKey(serviceInterfaceName,method);
        callbackParamType.put(key,paramClz);
    }

    /**
     * @param method
     * @throws com.jiaxy.ssf.exception.InitException if more callback param
     * @return true if the method has only one callback param
     */
    private static boolean isCallbackMethod(Method method){
        Class[] paramsTypes = method.getParameterTypes();
        int callbackParams = 0;
        for (Class paramsType:paramsTypes){
            if (isCallback(paramsType)){
                callbackParams++;
            }
        }
        if (callbackParams > 1){
            throw new InitException(String.format("method[%s] has more than one callback param",method.getName()));
        }
        if (callbackParams == 1){
            return true;
        }
        return false;
    }



    private static String callbackRegistryKey(String serviceInterfaceName,String method){
        return serviceInterfaceName+":"+method;
    }


}
