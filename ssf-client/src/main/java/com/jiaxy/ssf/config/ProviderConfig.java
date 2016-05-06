package com.jiaxy.ssf.config;

import com.jiaxy.ssf.common.Assert;
import com.jiaxy.ssf.exception.IllegalConfigureException;
import com.jiaxy.ssf.intercept.MessageInvocation;
import com.jiaxy.ssf.intercept.MessageInvocationFactory;
import com.jiaxy.ssf.processor.ProcessorManagerFactory;
import com.jiaxy.ssf.processor.ProviderProcessor;
import com.jiaxy.ssf.server.ProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/07 17:49
 */
public class ProviderConfig<T> extends SSFConfig {

    private static final Logger logger = LoggerFactory.getLogger(ProviderConfig.class);


    private T ref;




    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }


    @Override
    public Class<?> getProxyClass() {
        return getServiceInterfaceClass();
    }

    public void doExport() {
        ProviderConfig exported = ProviderManager.getExportedProvider(this);
        String uniqueKey = buildUniqueKey();
        if ( exported != null ){
            logger.warn("duplicate provider config:{},check it",uniqueKey);
            return;
        } else {
            synchronized (this){
                Assert.notNull(alias,"alias is empty");
                Assert.notNull(serviceInterfaceName,"serviceInterfaceName is empty");
                Assert.notNull(ref,String.format("the implement of the %s instance is null",serviceInterfaceName));
                Class serviceClz = getProxyClass();
                if (!serviceClz.isInstance(ref)){
                    throw new IllegalConfigureException(String.format("%s is not instance of interface %s.please check ref",
                            ref.getClass().getName(),
                            serviceInterfaceName));
                }
                MessageInvocation invocation = MessageInvocationFactory.getMessageInvocation(this);
                ProviderProcessor providerProcessor = new ProviderProcessor(invocation);
                //register processor for this interface service
                //TODO bug fix
                ProcessorManagerFactory.getInstance().register(ProcessorManagerFactory.processorKey(serviceInterfaceName,alias),providerProcessor);
                ProviderManager.addExportedProvider(this);
                logger.info("export provider [{}] successfully.",uniqueKey);
            }
        }
    }


    public void unExport(){
        if ( ProviderManager.getExportedProvider(this) == null ){
            return;
        }
        String uniqueKey = buildUniqueKey();
        ProcessorManagerFactory.getInstance().unRegister(serviceInterfaceName);
        ProviderManager.removeExportedProvider(this);
        logger.info("unExport provider [%s] successfully.",uniqueKey);
    }

    @Override
    public String buildUniqueKey() {
        return "provider://"+serviceInterfaceName+":"+alias;
    }



}
