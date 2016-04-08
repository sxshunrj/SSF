package com.jiaxy.ssf.config;

import com.jiaxy.ssf.intercept.MessageInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/07 17:49
 */
public class ProviderConfig<T> {


    private T ref;


    private final List<MessageInterceptor> interceptors = new ArrayList<MessageInterceptor>();


    public List<MessageInterceptor> getInterceptors(){
        return interceptors;
    }

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }



    private void buildInterceptorChain(){

    }

}
