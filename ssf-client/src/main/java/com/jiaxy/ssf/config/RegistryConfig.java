package com.jiaxy.ssf.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/15 14:25
 */
public class RegistryConfig extends AbstractConfig{

    //ip:port
    private List<String> registerAddresses = new ArrayList<>();

    public List<String> getRegisterAddresses() {
        return registerAddresses;
    }

    public void setRegisterAddresses(List<String> registerAddresses) {
        this.registerAddresses = registerAddresses;
    }

    @Override
    public String buildUniqueKey() {
        return null;
    }
}
