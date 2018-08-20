package com.youmu.support.spring;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class RequestInfoHolder {
    private String uri;
    private RequestMethod requestMethod;
    private List<SpringParamInfoHolder> paramInfos;

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public List<SpringParamInfoHolder> getParamInfos() {
        return paramInfos;
    }

    public void setParamInfos(List<SpringParamInfoHolder> paramInfos) {
        this.paramInfos = paramInfos;
    }
}
