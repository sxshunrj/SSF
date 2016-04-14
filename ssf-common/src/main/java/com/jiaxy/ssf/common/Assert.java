package com.jiaxy.ssf.common;

import java.util.Collection;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/11 15:43
 */
public class Assert{


    public static void notNull(Object object,String message){
        if (object == null){
            throw new IllegalArgumentException(message);
        }
    }

    public static void notCollectionEmpty(Collection collection,String message){
        if ( collection == null ){
            throw new IllegalArgumentException(message);
        }
        if ( collection.isEmpty() ){
            throw new IllegalArgumentException(message);
        }
    }
}
