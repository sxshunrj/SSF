package com.jiaxy.ssf.service.impl;

import com.jiaxy.ssf.service.PojoBean;
import com.jiaxy.ssf.service.TestSuiteService;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/14 16:14
 */
public class TestSuiteServiceImpl implements TestSuiteService{

    @Override
    public String echo() {
        return "echo";
    }

    @Override
    public String helloWorld(String str) {
        return str +" ssf hello world!";
    }

    @Override
    public void hello(PojoBean bean) {
        System.out.println( bean != null ? bean.toString() :"bean is null");

    }
}
