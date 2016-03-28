package com.jiaxy.ssf.common;

import java.nio.charset.Charset;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @since 2016/03/17 13:37
 */
public class Constants {

    /**
     * connection timeout (milliseconds)
     */
    public static final int DEFAULT_CLIENT_CONNECT_TIMEOUT = 5000;

    public static final int CPU_CORES = CommonUtil.getCpuCores();

    public static final boolean WINDOWS = CommonUtil.isWindows();

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
}
