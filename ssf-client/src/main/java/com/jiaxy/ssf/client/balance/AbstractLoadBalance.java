package com.jiaxy.ssf.client.balance;

import com.jiaxy.ssf.client.LoadBalance;
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


    protected ProvidersWeight getProviders(List<Provider> providers){
        ProvidersWeight providersInfo = new ProvidersWeight();
        for ( int i = 0 ;i < providers.size();i = i + 2){
            Provider provider = providers.get(i);
            providersInfo.setWeightTotal(providersInfo.getWeightTotal()+provider.getWeight());
            if (i + 1 < providers.size()){
                Provider nextProvider = providers.get(i+1);
                providersInfo.setWeightTotal(providersInfo.getWeightTotal() + nextProvider.getWeight());
                if (providersInfo.isIdenticalWeight() && provider.getWeight() != nextProvider.getWeight()){
                    providersInfo.setIdenticalWeight(false);
                }
            }
        }
        return providersInfo;
    }


    protected class ProvidersWeight {

        private boolean identicalWeight = true;

        private int weightTotal;

        public ProvidersWeight() {
        }

        public boolean isIdenticalWeight() {
            return identicalWeight;
        }

        public void setIdenticalWeight(boolean identicalWeight) {
            this.identicalWeight = identicalWeight;
        }

        public int getWeightTotal() {
            return weightTotal;
        }

        public void setWeightTotal(int weightTotal) {
            this.weightTotal = weightTotal;
        }
    }
}
