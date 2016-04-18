package com.jiaxy.ssf.client;

import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.registry.Provider;

import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 10:21
 */
public interface LoadBalance {

    Provider select(RequestMessage req,List<Provider> providers);
}
