package com.jiaxy.ssf.regcenter.common;

import com.jiaxy.ssf.common.bo.SubscribeURL;
import io.atomix.copycat.Command;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/12 17:50
 */
public class SubscribeCommand implements Command<SubscribeURL> {

    private final SubscribeURL subscribeURL;

    public SubscribeCommand(SubscribeURL subscribeURL) {
        this.subscribeURL = subscribeURL;
    }

    public SubscribeURL subscribeURL(){
        return subscribeURL;
    }
    @Override
    public CompactionMode compaction() {
        return CompactionMode.QUORUM;
    }
}
