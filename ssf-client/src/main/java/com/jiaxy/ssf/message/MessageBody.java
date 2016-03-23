package com.jiaxy.ssf.message;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 16:57
 */
public class MessageBody implements Serializable {


    private String className;

    private String methodName;

    private String alias;

    private String[] argsType;

    private Object[] args;

    private Map<String,Object> attachments = new HashMap<String, Object>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String[] getArgsType() {
        return argsType;
    }

    public void setArgsType(String[] argsType) {
        this.argsType = argsType;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "MessageBody{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", alias='" + alias + '\'' +
                ", argsType=" + Arrays.toString(argsType) +
                ", args=" + Arrays.toString(args) +
                ", attachments=" + attachments +
                '}';
    }
}
