package com.youmu.support.spring.serviceinvoker.httpclient;

import com.youmu.common.Factory;
import org.apache.http.impl.client.CloseableHttpClient;


/**
 * best to create a cached httpclient,cause it will don't shutdown after that
 * use to one request
 * @param <T> httpclient type
 */
public interface HttpClientFactory<T extends CloseableHttpClient> extends Factory<T> {
}
