package com.youmu.support.spring;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class HttpClientServiceConfiguration extends ServiceConfiguration {

    private HttpClientFactory httpClientFactory;

    public HttpClientFactory getHttpClientFactory() {
        return httpClientFactory;
    }

    public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }
}
