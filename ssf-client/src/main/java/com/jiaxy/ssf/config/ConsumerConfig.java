package com.jiaxy.ssf.config;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/11 18:34
 */
public class ConsumerConfig<T> extends SSFConfig{

    private T proxy;


    public T refer(){
        return proxy;
    }


    public void unRefer(){

    }


    @Override
    public String buildUniqueKey() {
        return null;
    }
}
