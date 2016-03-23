package com.jiaxy.ssf.common;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/17 16:06
 */
public class CommonUtil {


    /**
     *
     * @return cpu 核数
     */
    public static int getCpuCores(){
        return Runtime.getRuntime().availableProcessors();
    }


    public static boolean isWindows(){
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("windows") > -1 ){
            return true;
        }
        return false;
    }
}
