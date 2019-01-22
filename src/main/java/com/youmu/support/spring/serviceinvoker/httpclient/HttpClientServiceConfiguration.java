package com.youmu.support.spring.serviceinvoker.httpclient;

import com.youmu.support.spring.serviceinvoker.core.HttpServiceConfiguration;
import com.youmu.support.spring.serviceinvoker.httpclient.HttpClientResponseHandler;
import com.youmu.support.spring.serviceinvoker.httpclient.impl.DefaultHttpClientResponseHandler;
import com.youmu.support.spring.serviceinvoker.httpclient.HttpClientFactory;

import java.util.Map;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class HttpClientServiceConfiguration extends HttpServiceConfiguration {

    /**
     * httpclient的工厂通过自定义来实现服务调用时使用的httpclient
     */
    private HttpClientFactory httpClientFactory;

    /**
     * 服务调用时候会附带的默认头，如果入参有则会覆盖此header
     */
    private Map<String, String> headers;

    /**
     * 结果处理器
     */
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
