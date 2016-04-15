package com.jiaxy.ssf.client.balance;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/15 14:12
 */
public enum LoadBalanceStrategy {

    RANDOM,

    ROUNDROBIN,

    LEASTACTIVE,

    CONSISTENTHASH;
}
