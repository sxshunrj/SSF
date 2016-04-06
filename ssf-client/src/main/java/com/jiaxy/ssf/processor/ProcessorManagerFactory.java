package com.jiaxy.ssf.processor;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/06 14:54
 */
public class ProcessorManagerFactory {

    private static final ProcessorManager instance = new DefaultProcessorManager();

    public static ProcessorManager getInstance(){
        return instance;
    }


    public static String processorKey(String serviceName,String alias){
        return serviceName+":"+alias;

    }


    public static String processorKey(String serverHost, int serverPort){
        return serverHost+":"+serverPort;
    }
}
