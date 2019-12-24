package com.youmu.support.spring.serviceinvoker.request;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class RequestInfoHolder {
    private String uri;
    private RequestMethod requestMethod;
    private List<SpringParamInfoHolder> paramInfos;
    private MediaType contentType;
    private Charset charset;

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

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

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    public MediaType getMergedContentType() {
        if (null == contentType) {
            return null;
        }
        if (null != charset) {
            return new MediaType(contentType, charset);
        }
        return contentType;
    }
}
