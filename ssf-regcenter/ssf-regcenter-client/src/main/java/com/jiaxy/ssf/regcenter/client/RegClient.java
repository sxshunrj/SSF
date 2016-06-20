package com.jiaxy.ssf.regcenter.client;

import com.jiaxy.ssf.common.bo.SSFURL;
import com.jiaxy.ssf.common.bo.SubscribeURL;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/20 10:57
 */
public interface RegClient {


    /**
     * register service provider
     * @param ssfurl
     * @return
     */
    public SSFURL register(SSFURL ssfurl);


    /**
     * unRegister service provider that is this provider is offline.
     * @param ssfurl
     * @return
     */
    public boolean unRegister(SSFURL ssfurl);


    /**
     * subscribe the service
     *
     * @param subscribeURL
     * @return
     */
    public SubscribeURL subscribe(SubscribeURL subscribeURL);


    /**
     * unSubscribe the service
     * @param subscribeURL
     * @return
     */
    public boolean unSubscribe(SubscribeURL subscribeURL);


}
