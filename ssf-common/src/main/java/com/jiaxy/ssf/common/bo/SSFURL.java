package com.jiaxy.ssf.common.bo;

import java.io.Serializable;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/12 17:24
 */
public class SSFURL implements Serializable{

    private String ip;

    private int port;

    private int pid;

    private String serviceName;

    private String alias;

    private int protocol;

    private long startTime;

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

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SSFURL ssfurl = (SSFURL) o;

        if (pid != ssfurl.pid) return false;
        if (port != ssfurl.port) return false;
        if (protocol != ssfurl.protocol) return false;
        if (alias != null ? !alias.equals(ssfurl.alias) : ssfurl.alias != null) return false;
        if (ip != null ? !ip.equals(ssfurl.ip) : ssfurl.ip != null) return false;
        if (serviceName != null ? !serviceName.equals(ssfurl.serviceName) : ssfurl.serviceName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + pid;
        result = 31 * result + (serviceName != null ? serviceName.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + protocol;
        return result;
    }

    @Override
    public String toString() {
        return "SSFURL{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", pid=" + pid +
                ", serviceName='" + serviceName + '\'' +
                ", alias='" + alias + '\'' +
                ", protocol=" + protocol +
                ", startTime=" + startTime +
                '}';
    }
}
