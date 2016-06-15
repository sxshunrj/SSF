package com.jiaxy.ssf.common.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/14 11:04
 */
public class SubscribeURL implements Serializable{

    private int type;

    private SSFURL sourceURL;

    private List<SSFURL> providerList;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SSFURL getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(SSFURL sourceURL) {
        this.sourceURL = sourceURL;
    }

    public List<SSFURL> getProviderList() {
        return providerList;
    }

    public void setProviderList(List<SSFURL> providerList) {
        this.providerList = providerList;
    }
}