package com.jiaxy.ssf.registry;

import com.jiaxy.ssf.exception.InitException;

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

    /**
     * direct url
     * <pre>
     *     ssf://172.0.0.1:31616?weight=100
     * </pre>
     * @param url
     * @return
     */
    public static Provider build(String url){
        String sourceURL = url;
        try {
            Provider provider = new Provider();
            int protocolIndex = url.indexOf("://");
            url = url.substring(protocolIndex + 3, url.length());
            int portIndex = url.indexOf(":");
            String ip = url.substring(0, portIndex);
            provider.setIp(ip);
            int paramsIndex = url.indexOf("?");
            String port = url.substring(portIndex + 1, paramsIndex == -1 ? url.length():paramsIndex);
            provider.setPort(Integer.valueOf(port));
            if (paramsIndex > -1){
                url = url.substring(paramsIndex + 1, url.length());
                String[] params = url.split("&");
                if (params != null && params.length > 0) {
                    for (String param : params) {
                        String[] paramPair = param.split("=");
                        if ("weight".equalsIgnoreCase(paramPair[0])) {
                            provider.setWeight(Integer.valueOf(paramPair[1]));
                        }
                    }
                }
            }
            return provider;
        } catch (Exception e) {
            throw new InitException(String.format("%s url is invalid.valid url format is ssf://ip:port", sourceURL));
        }
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
