package com.jiaxy.ssf.service;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *     callback interface
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/09 18:01
 */
public interface Callback<I,O> {


    /**
     *
     * @param ipo input param object
     *
     * @return
     */
    O callback(I ipo);

}
