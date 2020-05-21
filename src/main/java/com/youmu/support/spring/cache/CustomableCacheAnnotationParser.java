package com.youmu.support.spring.cache;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;

import com.youmu.support.spring.cache.annotation.Expireable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/09/16
 */
public class CustomableCacheAnnotationParser extends SpringCacheAnnotationParser
        implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(CustomableCacheAnnotationParser.class);

    private CacheAnnotationHandler cacheAnnotationHandler;

    public CustomableCacheAnnotationParser() {
    }

    public CustomableCacheAnnotationParser(CacheAnnotationHandler cacheAnnotationHandler) {
        this.cacheAnnotationHandler = cacheAnnotationHandler;
    }

    @Override
    public Collection<CacheOperation> parseCacheAnnotations(Class<?> type) {
        // dont support for type expire
        Collection<CacheOperation> cacheOperations = super.parseCacheAnnotations(type);
        if (!CollectionUtils.isEmpty(cacheOperations)) {

        }
        return cacheOperations;
    }

    @Override
    public Collection<CacheOperation> parseCacheAnnotations(Method method) {
        Collection<CacheOperation> cacheOperations = super.parseCacheAnnotations(method);
        if (!CollectionUtils.isEmpty(cacheOperations)) {
            Expireable expireable = AnnotatedElementUtils.findMergedAnnotation(method,
                    Expireable.class);
            if (null != expireable) {
                cacheAnnotationHandler.handle(expireable, method);
            }
        }
        return cacheOperations;
    }

}
