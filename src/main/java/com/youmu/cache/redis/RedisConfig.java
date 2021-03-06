package com.youmu.cache.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

import com.youmu.cache.CacheAnnotationHandler;
import com.youmu.cache.CustomableCacheAnnotationParser;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@Configuration
public class RedisConfig {

    @Bean
    public CacheAnnotationHandler cacheAnnotationHandler(RedisCacheManager redisCacheManager) {
        return new DefaultRedisCacheAnnotationHandler(redisCacheManager);
    }

    @Bean
    public CustomableCacheAnnotationParser customableCacheAnnotationParser(
            RedisCacheManager redisCacheManager) {
        return new CustomableCacheAnnotationParser(cacheAnnotationHandler(redisCacheManager));
    }
}
