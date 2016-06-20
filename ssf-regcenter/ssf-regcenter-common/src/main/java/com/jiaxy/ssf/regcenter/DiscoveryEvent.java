package com.jiaxy.ssf.regcenter;

import com.jiaxy.ssf.common.bo.SSFURL;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/20 13:30
 */
@FunctionalInterface
public interface DiscoveryEvent {


    /**
     * service provider add or remove
     *
     * @param ssfurl
     */
    public void discovery(SSFURL ssfurl);


}
