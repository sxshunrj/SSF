package com.jiaxy.ssf.client.balance;

import com.jiaxy.ssf.message.RequestMessage;
import com.jiaxy.ssf.registry.Provider;

import java.util.List;
import java.util.Random;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/18 10:39
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    private Random random = new Random();

    @Override
    public Provider doSelect(RequestMessage req, List<Provider> providers) {
        sortProviderListByWeight(providers);
        boolean theSameWeight = false;
        int maxWeight = providers.get(0).getWeight();
        int minWeight = providers.get(providers.size()-1).getWeight();
        if (maxWeight == minWeight){
            theSameWeight = true;
        }
        Provider selected = null;
        if (!theSameWeight){
            int randomWeight = random.nextInt(maxWeight);
            //TODO no loop
            for (int i = 0 ;i<providers.size();i++){
                Provider provider = providers.get(i);
                if (provider.getWeight() - randomWeight >= 0){
                    selected = providers.get(i);
                    break;
                }
            }
        } else {
            selected = providers.get(random.nextInt(providers.size()));
        }
        return selected ;
    }
}
