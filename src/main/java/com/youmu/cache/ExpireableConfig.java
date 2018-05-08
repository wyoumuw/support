package com.youmu.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExpireableConfig {

    @Autowired(required = false)
    private CustomableCacheAnnotationParser cacheAnnotationParser;

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheOperationSource cacheOperationSource() {
        return new AnnotationCacheOperationSource((null == cacheAnnotationParser)
                ? new CustomableCacheAnnotationParser() : cacheAnnotationParser);
    }
}
