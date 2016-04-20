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

    public Provider(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Provider(String ip, int port, int weight) {
        this.ip = ip;
        this.port = port;
        this.weight = weight;
    }

    public Provider() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Provider provider = (Provider) o;

        if (port != provider.port) return false;
        if (weight != provider.weight) return false;
        if (ip != null ? !ip.equals(provider.ip) : provider.ip != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + weight;
        return result;
    }

    @Override
    public String toString() {
        return "Provider{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", weight=" + weight +
                '}';
    }
}
