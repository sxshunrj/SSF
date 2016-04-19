package com.jiaxy.ssf.registry;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 *
 *     service provider
 *
 * </p>
 *
 * @since 2016/03/16 18:36
 */
public class Provider {

    private String ip;

    private int port;

    private int weight;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
