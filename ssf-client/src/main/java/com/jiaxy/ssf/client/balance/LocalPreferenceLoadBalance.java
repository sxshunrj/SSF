package com.jiaxy.ssf.client.balance;

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
 * @since 2016/04/18 10:43
 */
public class LocalPreferenceLoadBalance extends AbstractLoadBalance {
    @Override
    public Provider doSelect(RequestMessage req, List<Provider> providers) {
        return null;
    }
}
