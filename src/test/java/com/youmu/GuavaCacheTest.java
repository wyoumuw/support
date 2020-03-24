/*
 * Copyright (c) 2001-2020 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */
package com.youmu;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * guava的cache测试
 *
 * @author YOUMU
 * @version V1.0
 * @since 2020-01-09 16:34
 */
public class GuavaCacheTest {

    @Test
    public void loadingCache() throws InterruptedException, ExecutionException {
        Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
        for (int i = 0; i < 10; i++) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(3));
//            String key = "" + (i % 2);
            String key="1";
            Object obj = cache.get(key, Object::new);
            System.out.println(key + ":" + obj);
            if (i   == 5) {
                System.out.println("put:"+key+",obj:"+obj);
                cache.put(key,obj);
            }
        }
        System.out.println("over");
    }
}
