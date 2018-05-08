package com.youmu.cache;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Lists;
import com.youmu.cache.annotation.Expireable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheAnnotationParser;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
            cacheAnnotationHandler.handle(expireable, method);
        }
        return cacheOperations;
    }

}
