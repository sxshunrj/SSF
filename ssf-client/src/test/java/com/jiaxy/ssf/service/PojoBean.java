package com.jiaxy.ssf.service;

import java.io.Serializable;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/05/09 10:26
 */
public class PojoBean implements Serializable{

    private String beanId;

    private int id;

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PojoBean{" +
                "beanId='" + beanId + '\'' +
                ", id=" + id +
                '}';
    }
}
