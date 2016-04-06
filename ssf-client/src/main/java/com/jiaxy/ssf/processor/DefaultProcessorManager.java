package com.jiaxy.ssf.processor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/05 18:45
 */
public class DefaultProcessorManager implements ProcessorManager {

    private ConcurrentHashMap<String,Processor> processors = new ConcurrentHashMap<String, Processor>();

    @Override
    public void register(String key, Processor processor) {
        processors.putIfAbsent(key, processor);
    }

    @Override
    public void unRegister(String key) {
        processors.remove(key);
    }

    @Override
    public Processor getProcessor(String key) {
        return processors.get(key);
    }

    @Override
    public Processor getProcessor(String key, Class processClz) {
        return null;
    }
}
