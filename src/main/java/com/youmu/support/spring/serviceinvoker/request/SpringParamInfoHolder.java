package com.youmu.support.spring.serviceinvoker.request;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2018/08/17
 */
public class SpringParamInfoHolder {

    public enum ParamType {
        PATH_VARIABLE, REQUEST_PARAM, REQUEST_BODY, REQUEST_HEADER, UNKNOWN
    }

    private String name;

    private ParamType paramType;

    private Class<?> type;

    public SpringParamInfoHolder(String name, Class<?> type, ParamType paramType) {
        this.name = name;
        this.paramType = paramType;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
