package com.jiaxy.ssf.service;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     Generic invoke.
 *
 *     no need the service interface class
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/15 15:14
 */
public interface GenericService {


    Object $invoke(String method,String[] argsTypes,Object[] args);

    Object $asyncInvoke(String method,String[] argsTypes,Object[] args);
}
