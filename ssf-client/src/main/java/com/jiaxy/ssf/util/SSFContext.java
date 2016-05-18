package com.jiaxy.ssf.util;

import com.jiaxy.ssf.common.StringUtil;

import java.lang.management.ManagementFactory;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/14 10:41
 */
public class SSFContext {


    private static String LOCAL_HOST;

    private static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];


    public static String getLocalHost(){
        if (!StringUtil.isEmpty(LOCAL_HOST)){
            return LOCAL_HOST;
        } else {
            LOCAL_HOST = NetUtil.getLocalHost();
            return LOCAL_HOST;
        }
    }

    public static int getPID(){
        return Integer.valueOf(PID);
    }

}
