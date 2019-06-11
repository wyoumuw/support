package com.youmu.support.spring;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.List;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class SpringHttpClientServiceConfiguration extends HttpClientServiceConfiguration {
    private ConversionService conversionService;

    private List<HttpMessageConverter<?>> httpMessageConverters;

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
}
