package com.jiaxy.ssf.processor;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/05 17:56
 */
public interface ProcessorManager<T extends Processor> {

    void register(String key,T processor);

    void unRegister(String key);

    T getProcessor(String key);


}
