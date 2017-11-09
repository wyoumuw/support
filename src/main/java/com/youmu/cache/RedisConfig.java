package com.youmu.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@Configuration
public class RedisConfig {

    @Bean
    @Conditional(BeanCondition.class)
    public CacheAnnotationHandler cacheAnnotationHandler(RedisCacheManager redisCacheManager) {
        return new DefaultRedisCacheAnnotationHandler(redisCacheManager);
    }
}
