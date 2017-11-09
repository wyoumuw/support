package com.youmu.cache;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@Configuration
public class ExpireableConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CustomableCacheAdvisor customableCacheAdvisor(
            CacheAnnotationHandler cacheAnnotationHandler) {
        return new CustomableCacheAdvisor(customableCacheInterceptor(cacheAnnotationHandler));
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CustomableCacheInterceptor customableCacheInterceptor(
            CacheAnnotationHandler cacheAnnotationHandler) {
        return new CustomableCacheInterceptor(cacheAnnotationHandler);
    }
}
