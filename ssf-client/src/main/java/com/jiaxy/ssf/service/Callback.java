package com.jiaxy.ssf.service;

import java.io.Serializable;

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
public interface Callback<I,O> extends Serializable {


    /**
     *
     * @param ipo input param object
     *
     * @return
     */
    O callback(I ipo) throws RuntimeException;

}
