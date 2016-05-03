package com.jiaxy.ssf.client.balance;

import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.registry.Provider;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 10:41
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance{

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public Provider doSelect(RequestMessage req, List<Provider> providers) {
        int size = providers.size();
        int selected;
        do {
            selected = increment() % size;
            Provider provider = providers.get(selected);
            if ( provider.getWeight() > 0 ){
                return provider;
            }
        } while ( selected != size -1 );
        return null;
    }

    private int increment(){
        int v = index.incrementAndGet();
        return v & 0x7FFFFFFF;
    }
}
