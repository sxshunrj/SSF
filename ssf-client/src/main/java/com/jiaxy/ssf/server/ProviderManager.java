package com.jiaxy.ssf.server;

import com.jiaxy.ssf.config.ProviderConfig;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/12 21:33
 */
public class ProviderManager {


    private static final ConcurrentHashMap<String,ProviderConfig> providerExported = new ConcurrentHashMap<String, ProviderConfig>();

    public static void addExportedProvider(ProviderConfig config){
        providerExported.put(config.buildUniqueKey(),config);
    }


    public static void removeExportedProvider(ProviderConfig config){
        providerExported.remove(config.buildUniqueKey(), config);
    }

    public static ProviderConfig getExportedProvider(ProviderConfig config){
        return providerExported.get(config.buildUniqueKey());
    }
}
