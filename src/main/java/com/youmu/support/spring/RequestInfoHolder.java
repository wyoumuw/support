package com.youmu.support.spring;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class RequestInfoHolder {
    private String uri;
    private RequestMethod requestMethod;
    private List<String> paramNames;

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setParamNames(List<String> paramNames) {
        this.paramNames = paramNames;
    }

    public String getUri() {
        return uri;
    }

    public List<String> getParamNames() {
        return paramNames;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }
}
