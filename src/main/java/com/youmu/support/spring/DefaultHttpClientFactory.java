package com.youmu.support.spring;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class DefaultHttpClientFactory implements HttpClientFactory<CloseableHttpClient> {
    @Override
    public CloseableHttpClient get() {
        return HttpClients.createDefault();
    }
}
