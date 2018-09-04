package com.youmu.support.spring.serviceinvoker;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class HttpClientServiceConfiguration extends ServiceConfiguration {

    private HttpClientFactory httpClientFactory;

    private HttpClientResponseHandler httpClientResponseHandler = new DefaultHttpClientResponseHandler(
            this);

    public HttpClientFactory getHttpClientFactory() {
        return httpClientFactory;
    }

    public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    public void checkHttpClientConfiguration() {
        super.checkServiceConfiguration();
        if (null != this.httpClientFactory) {
            throw new IllegalArgumentException("HttpClientServiceConfiguration is null");
        }

    }

    public HttpClientResponseHandler getHttpClientResponseHandler() {
        return httpClientResponseHandler;
    }

    public void setHttpClientResponseHandler(HttpClientResponseHandler httpClientResponseHandler) {
        this.httpClientResponseHandler = httpClientResponseHandler;
    }
}
