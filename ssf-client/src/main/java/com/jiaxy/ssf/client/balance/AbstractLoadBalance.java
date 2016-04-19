package com.jiaxy.ssf.client.balance;

import com.jiaxy.ssf.client.LoadBalance;
import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.registry.Provider;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 10:37
 */
public abstract class AbstractLoadBalance implements LoadBalance {


    @Override
    public Provider select(RequestMessage req, List<Provider> providers) {
        if (providers == null || providers.isEmpty()){
            return null;
        }
        if (providers.size() == 1){
            return providers.get(0);
        }
        return doSelect(req,providers);
    }

    public abstract Provider doSelect(RequestMessage req,List<Provider> providers);


    protected void sortProviderListByWeight(List<Provider> providers){
        if (providers != null && !providers.isEmpty()){
            Collections.sort(providers, new Comparator<Provider>() {
                @Override
                public int compare(Provider o1, Provider o2) {
                    return o1.getWeight() > o2.getWeight() ? 1 : -1;
                }
            });
        }
    }
}
