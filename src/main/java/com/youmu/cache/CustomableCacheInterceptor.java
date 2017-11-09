package com.youmu.cache;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.youmu.cache.annotation.Expireable;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/08
 */
public class CustomableCacheInterceptor implements MethodInterceptor {

    private CacheAnnotationHandler cacheAnnotationHandler;

    public CustomableCacheInterceptor(CacheAnnotationHandler cacheAnnotationHandler) {
        this.cacheAnnotationHandler = cacheAnnotationHandler;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        CacheAnnotationHandler handler = getCacheAnnotationHandler();
        Expireable expireable = null;
        if (null != handler && null != (expireable = AnnotatedElementUtils
                .getMergedAnnotation(invocation.getMethod(), Expireable.class))) {
            CacheAnnotationHandler.HandleResult res = handler.handle(expireable,
                    invocation);
            if (null != res && !res.getDoChain()) {
                return res.getRtnVal();
            }
        }
        return invocation.proceed();
    }

    public CacheAnnotationHandler getCacheAnnotationHandler() {
        return cacheAnnotationHandler;
    }

    public void setCacheAnnotationHandler(CacheAnnotationHandler cacheAnnotationHandler) {
        this.cacheAnnotationHandler = cacheAnnotationHandler;
    }
}
