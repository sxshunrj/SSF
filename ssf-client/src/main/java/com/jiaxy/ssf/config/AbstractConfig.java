package com.jiaxy.ssf.config;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/11 18:39
 */
public abstract class AbstractConfig {

    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    protected String id;


    public abstract String buildUniqueKey();

    protected String getConfigID(){
        if ( id == null ){
            return "ssf-config-id#"+idGenerator.incrementAndGet();
        } else {
            return id;
        }
    }

    public void setId(String id) {
        this.id = id;
    }

}
