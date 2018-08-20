package com.youmu.support.spring;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.List;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class ServiceConfiguration {
    private String server;

    private ConversionService conversionService;

    private List<HttpMessageConverter<?>> httpMessageConverters;

    private List<Class> ignoreParamClass;

    public ConversionService getConversionService() {
        return conversionService;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public List<HttpMessageConverter<?>> getHttpMessageConverters() {
        return httpMessageConverters;
    }

    public void setHttpMessageConverters(List<HttpMessageConverter<?>> httpMessageConverters) {
        this.httpMessageConverters = httpMessageConverters;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public List<Class> getIgnoreParamClass() {
        return ignoreParamClass;
    }

    public void setIgnoreParamClass(List<Class> ignoreParamClass) {
        this.ignoreParamClass = ignoreParamClass;
    }
}
