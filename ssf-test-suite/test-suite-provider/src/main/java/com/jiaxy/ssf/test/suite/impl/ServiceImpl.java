package com.jiaxy.ssf.test.suite.impl;

import com.jiaxy.ssf.service.Callback;
import com.jiaxy.ssf.test.suite.IService;

import java.util.concurrent.TimeUnit;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 * @since 2016/05/16 11:04
 */
public class ServiceImpl implements IService {

    @Override
    public String registryWithCallback(String ip,Callback<String,String> callback){
        if (callback != null){
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return callback.callback(ip) +" message from service";
        } else {
            return "no callback";
        }
    }
}
