package com.jiaxy.ssf.test.suite;

import com.jiaxy.ssf.service.Callback;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/16 11:01
 */
public interface IService {

    String registryWithCallback(String ip,Callback<String,String> callback);
}
