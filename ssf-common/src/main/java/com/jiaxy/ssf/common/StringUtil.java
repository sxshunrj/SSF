package com.jiaxy.ssf.common;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/14 13:17
 */
public class StringUtil {


    public static boolean isEmpty(String str){
        if( str == null || "".equals(str)){
            return true;
        } else {
            return false;
        }
    }
}
