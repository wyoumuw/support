package com.youmu.utils;

import com.youmu.support.spring.serviceinvoker.core.HttpServiceConfiguration;
import com.youmu.support.spring.serviceinvoker.core.ServiceScannerConfigure;
import com.youmu.support.spring.serviceinvoker.httpclient.HttpClientFactory;
import com.youmu.support.spring.serviceinvoker.httpclient.HttpClientServiceConfiguration;
import com.youmu.support.spring.serviceinvoker.httpclient.impl.DefaultHttpClientFactory;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * @Author: YOUMU
 * @Description: java8 Stream相关
 * @Date: 2018/11/12
 */
public final class StreamUtils {

    private StreamUtils() {
    }

    /**
     * 获取collection的stream如果collection是null则返回空stream
     * @param collection 要获取stream的collection
     * @param <T> collection的内容
     * @return 非null的Stream
     */
    public static <T> Stream<T> ofNullable(Collection<T> collection) {
        if (null == collection) {
            return Stream.empty();
        }
        return collection.stream();
    }
}
