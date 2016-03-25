package com.jiaxy.ssf.common;

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
     * 连接建立超时时间（毫秒）
     */
    public static final int DEFAULT_CLIENT_CONNECT_TIMEOUT = 5000;

    public static final int CPU_CORES = CommonUtil.getCpuCores();

    public static final boolean WINDOWS = CommonUtil.isWindows();
}
