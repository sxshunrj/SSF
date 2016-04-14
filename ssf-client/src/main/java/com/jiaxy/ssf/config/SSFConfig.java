package com.jiaxy.ssf.config;

import com.jiaxy.ssf.intercept.MessageInterceptor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/11 18:40
 */
public abstract class SSFConfig extends AbstractConfig {

    private AtomicInteger idGenerator = new AtomicInteger(0);

    private String id;

    /**
     * the service interface class name
     */
    protected String serviceInterfaceName;

    protected String alias;

    /**
     * the service interface proxy class
     */
    protected Class serviceProxyClz;

    protected List<MessageInterceptor> interceptors;

    protected String getConfigID(){
        if ( id == null ){
            return "ssf-config-id#"+idGenerator.incrementAndGet();
        } else {
            return id;
        }
    }

    public abstract String buildUniqueKey();

    public void setId(String id) {
        this.id = id;
    }

    public List<MessageInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<MessageInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getServiceInterfaceName() {
        return serviceInterfaceName;
    }

    public void setServiceInterfaceName(String serviceInterfaceName) {
        this.serviceInterfaceName = serviceInterfaceName;
    }
}
