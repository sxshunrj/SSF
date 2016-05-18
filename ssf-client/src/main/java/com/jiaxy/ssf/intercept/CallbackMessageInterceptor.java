package com.jiaxy.ssf.intercept;

import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.message.ResponseMessage;
import com.jiaxy.ssf.service.Callback;
import com.jiaxy.ssf.util.SSFContext;

import static com.jiaxy.ssf.util.Callbacks.*;
import static com.jiaxy.ssf.message.MessageHead.HeadKey.CALLBACK_INSTANCE_ID;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/12 15:01
 */
public class CallbackMessageInterceptor implements MessageInterceptor {

    @Override
    public ResponseMessage invoke(MessageInvocation invocation, RequestMessage message) throws Throwable {
        if (message.getRequestMessageBody() != null &&
                isCallbackMethod(message.getServiceName(),message.getMethodName())
                ){
            Class[] argsTypeClasses = message.getRequestMessageBody().getArgsTypeClasses();
            Object[] args = message.getRequestMessageBody().getArgs();
            for (int i = 0 ;i < argsTypeClasses.length;i++){
                Class argsTypeClass = argsTypeClasses[i];
                if (isCallback(argsTypeClass)){
                    Callback callback = (Callback) args[i];
                    //callback instance no need
                    String callbackInstanceId = buildCallbackInstanceId(SSFContext.getLocalHost(),SSFContext.getPID(),callback);
                    message.getHead().addHeadKey(CALLBACK_INSTANCE_ID,callbackInstanceId);
                    callbackInstanceRegister(callbackInstanceId,callback);
                    args[i] = null;
                    break;
                }
            }
        }
        return invocation.proceed(message);
    }
}
