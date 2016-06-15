package com.jiaxy.ssf.regcenter.common;

import com.jiaxy.ssf.common.bo.SSFURL;
import io.atomix.copycat.Command;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/12 17:33
 */
public class RegisterCommand implements Command<SSFURL> {

    private final SSFURL ssfurl;

    public RegisterCommand(SSFURL ssfurl) {
        this.ssfurl = ssfurl;
    }

    public SSFURL ssfurl(){
        return ssfurl;
    }

    @Override
    public CompactionMode compaction() {
        return CompactionMode.QUORUM;
    }
}
