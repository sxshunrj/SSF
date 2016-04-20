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
        ProvidersWeight providersInfo = getProviders(providers);
        Provider selected = null;
        if (!providersInfo.isIdenticalWeight()){
            int randomWeight = random.nextInt(providersInfo.getWeightTotal());
            for (int i = providers.size() - 1 ;i >= 0;i--){
                Provider provider = providers.get(i);
                randomWeight -= provider.getWeight();
                if (randomWeight < 0){
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
