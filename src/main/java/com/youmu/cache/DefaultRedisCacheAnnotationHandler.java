package com.youmu.cache;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Sets;
import com.youmu.cache.annotation.Expireable;
/**
 * @Author: YOUMU
 * @Description: 默认对redisManager的实现,未完成类注解缓存
 * @Date: 2017/11/08
 */
public class DefaultRedisCacheAnnotationHandler
        implements CacheAnnotationHandler, InitializingBean {

    private Map<String, Long> expires = new ConcurrentHashMap<>();

    private Set<Method> cachedMethods = ConcurrentHashMap.newKeySet();

    private RedisCacheManager redisCacheManager;

    @Override
    public HandleResult handle(Expireable expireable, MethodInvocation invocation) {
        Method method;
        // 做过处理的方法不再处理
        if (null == invocation || cachedMethods.contains(method = invocation.getMethod())) {
            return HandleResult.CONTINUE;
        }
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
        redisCacheManager.setExpires(expires);
        // last put method in cache for next check
        cachedMethods.add(method);
        return HandleResult.CONTINUE;
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
                expires.put(cacheName, expire);
            }
        }
    }

    private void putExpireCacheable(Collection<Cacheable> cacheables, long expire) {
        for (Cacheable cacheable : cacheables) {
            for (int i = 0; i < cacheable.cacheNames().length; i++) {
                String cacheName = cacheable.cacheNames()[i];
                expires.put(cacheName, expire);
            }
        }
    }

    public DefaultRedisCacheAnnotationHandler(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    public DefaultRedisCacheAnnotationHandler() {
    }

    public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == redisCacheManager) {
            throw new NullPointerException("redisCacheManager can not be null");
        }
    }
}
