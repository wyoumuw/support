package com.youmu.support.spring.cache;

import com.youmu.support.spring.cache.annotation.EnableExpireableCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExpireableConfig extends ProxyCachingConfiguration {

    @Autowired(required = false)
    private CustomableCacheAnnotationParser cacheAnnotationParser;

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Override
    public CacheOperationSource cacheOperationSource() {
        return new AnnotationCacheOperationSource(
            (null == cacheAnnotationParser) ? new CustomableCacheAnnotationParser() : cacheAnnotationParser);
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableCaching = AnnotationAttributes.fromMap(
            importMetadata.getAnnotationAttributes(EnableExpireableCache.class.getName(), false));
        if (this.enableCaching == null) {
            throw new IllegalArgumentException(
                "@EnableExpireableCache is not present on importing class " + importMetadata.getClassName());
        }
    }
}
