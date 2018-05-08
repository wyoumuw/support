package com.youmu.cache.redis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Sets;
import com.youmu.cache.CacheAnnotationHandler;
import com.youmu.cache.annotation.Expireable;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/05/08
 */
public class DefaultRedisCacheAnnotationHandler implements CacheAnnotationHandler, InitializingBean,
        ApplicationListener<ContextRefreshedEvent> {

    private static final Field redisCacheManagerConfigsField;
    private static final Field defaultConfigField;

    static {
        redisCacheManagerConfigsField = ReflectionUtils.findField(RedisCacheManager.class,
                "initialCacheConfiguration");
        defaultConfigField = ReflectionUtils.findField(RedisCacheManager.class,
                "defaultCacheConfig");
        if (null == redisCacheManagerConfigsField || null == defaultConfigField) {
            throw new RuntimeException(
                    "can not found initialCacheConfiguration in class 'RedisCacheManager' please check spring-data-redis version!");
        }
        ReflectionUtils.makeAccessible(redisCacheManagerConfigsField);
        ReflectionUtils.makeAccessible(defaultConfigField);
    }
    private RedisCacheManager redisCacheManager;

    private RedisCacheConfiguration defaultConfig;

    private Map<String, RedisCacheConfiguration> initialCacheConfiguration;

    public DefaultRedisCacheAnnotationHandler(RedisCacheManager redisCacheManager) {
        setRedisCacheManager(redisCacheManager);
    }

    public DefaultRedisCacheAnnotationHandler() {
    }

    @Override
    public void handle(Expireable expireable, Method method) {
        Long expire = (0 == expireable.expire() || TimeUnit.SECONDS.equals(expireable.timeUnit()))
                ? expireable.expire()
                : TimeUnit.SECONDS.convert(expireable.expire(), expireable.timeUnit());
        Collection<Cacheable> cacheables = AnnotatedElementUtils.getAllMergedAnnotations(method,
                Cacheable.class);
        Collection<CachePut> cachePuts = AnnotatedElementUtils.getAllMergedAnnotations(method,
                CachePut.class);
        Collection<Caching> cachings = AnnotatedElementUtils.getAllMergedAnnotations(method,
                Caching.class);
        if (!CollectionUtils.isEmpty(cacheables)) {
            // TODO extensible
            putExpireCacheable(cacheables, expire);
        }
        if (!CollectionUtils.isEmpty(cachePuts)) {
            // TODO extensible
            putExpireCachePut(cachePuts, expire);
        }
        if (!CollectionUtils.isEmpty(cachings)) {
            // TODO extensible
            putExpireCaching(cachings, expire);
        }
    }

    private void putExpireCaching(Collection<Caching> cachings, Long expire) {
        for (Caching caching : cachings) {
            putExpireCacheable(Sets.newHashSet(caching.cacheable()), expire);
            putExpireCachePut(Sets.newHashSet(caching.put()), expire);
        }
    }

    private void putExpireCachePut(Collection<CachePut> cachePuts, Long expire) {
        for (CachePut cachePut : cachePuts) {
            for (int i = 0; i < cachePut.cacheNames().length; i++) {
                String cacheName = cachePut.cacheNames()[i];
                putExpire(cacheName, defaultConfig.entryTtl(Duration.ofSeconds(expire)));
            }
        }
    }

    private void putExpireCacheable(Collection<Cacheable> cacheables, long expire) {
        for (Cacheable cacheable : cacheables) {
            for (int i = 0; i < cacheable.cacheNames().length; i++) {
                String cacheName = cacheable.cacheNames()[i];
                putExpire(cacheName, defaultConfig.entryTtl(Duration.ofSeconds(expire)));
            }
        }
    }

    private void putExpire(String cacheName, RedisCacheConfiguration configuration) {
        synchronized (redisCacheManager) {
            initialCacheConfiguration.put(cacheName, configuration);
        }
    }

    public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == redisCacheManager) {
            throw new NullPointerException("redisCacheManager can not be null");
        }
        initialCacheConfiguration = (Map<String, RedisCacheConfiguration>) redisCacheManagerConfigsField
                .get(redisCacheManager);
        defaultConfig = (RedisCacheConfiguration) defaultConfigField.get(redisCacheManager);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        redisCacheManager.initializeCaches();
    }
}
