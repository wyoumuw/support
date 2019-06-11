package com.youmu.support.spring;

import com.youmu.common.Factory;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public interface HttpClientFactory<T extends CloseableHttpClient> extends Factory<T> {
}
