package com.jiaxy.ssf.intercept;

import com.jiaxy.ssf.message.AbstractMessage;
import com.jiaxy.ssf.message.ResponseMessage;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/07 14:03
 */
public interface Invocation {


    ResponseMessage proceed(AbstractMessage message) throws Throwable;


}
