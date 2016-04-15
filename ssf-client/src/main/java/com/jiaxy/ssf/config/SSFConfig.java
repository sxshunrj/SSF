package com.jiaxy.ssf.config;

import com.jiaxy.ssf.exception.InitException;
import com.jiaxy.ssf.intercept.MessageInterceptor;

import java.util.List;
import java.util.Map;
import static com.jiaxy.ssf.common.ClassUtil.forName;
import static com.jiaxy.ssf.common.ClassUtil.getDefaultClassLoader;

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


    /**
     * the service interface class name
     */
    protected String serviceInterfaceName;

    protected String alias;

    protected List<MessageInterceptor> interceptors;

    protected List<RegistryConfig> registries;

    protected boolean registration = true;

    /**
     * method config
     */
    protected Map<String,MethodConfig> methodsConfig;

    protected Map<String,String> parameters;

    protected Compress compress;

    public abstract Class<?> getProxyClass();

    protected Class getServiceInterfaceClass(){
        try {
            return forName(serviceInterfaceName, getDefaultClassLoader(), true);
        } catch (ClassNotFoundException e) {
            throw new InitException(String.format("load service interface [%s] class error",
                    serviceInterfaceName),e);
        }
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

    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    public void setRegistries(List<RegistryConfig> registries) {
        this.registries = registries;
    }

    public boolean isRegistration() {
        return registration;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }


    public Map<String, MethodConfig> getMethodsConfig() {
        return methodsConfig;
    }

    public void setMethodsConfig(Map<String, MethodConfig> methodsConfig) {
        this.methodsConfig = methodsConfig;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Compress getCompress() {
        return compress;
    }

    public void setCompress(Compress compress) {
        this.compress = compress;
    }
}
