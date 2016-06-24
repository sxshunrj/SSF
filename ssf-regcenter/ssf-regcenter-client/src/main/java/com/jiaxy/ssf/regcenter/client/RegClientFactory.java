package com.jiaxy.ssf.regcenter.client;

import com.jiaxy.ssf.regcenter.client.copycat.CopycatRegClient;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     RegClient build factory.cache regClient
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/22 11:49
 */
public class RegClientFactory {

    private static final ConcurrentHashMap<String,RegClient> regClientCache = new ConcurrentHashMap<>();

    public static RegClient getRegClient(List<String> regAddresses){
        String key = buildKey(regAddresses);
        RegClient regClient = regClientCache.get(key);
        if (regClient == null){
            synchronized (RegClientFactory.class){
                regClient = regClientCache.get(key);
                if (regClient == null){
                    regClient = new CopycatRegClient(regAddresses);
                    regClientCache.put(key,regClient);
                    return regClient;
                }
            }
        }
        return regClient;
    }


    public static void removeRegClient(String key){
        RegClient regClient = regClientCache.get(key);
        if (regClient != null){
            regClient.close();
            regClientCache.remove(key);
        }
    }


    public static String buildKey(List<String> regServerAddresses){
        StringBuilder builder = new StringBuilder();
        for (String address:regServerAddresses){
            builder.append(address);
        }
        return builder.toString();
    }
}
