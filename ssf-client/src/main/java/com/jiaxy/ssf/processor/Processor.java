package com.jiaxy.ssf.processor;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/05 17:57
 */
public interface Processor<I,O> {

    O execute(I in) throws Throwable;

}
