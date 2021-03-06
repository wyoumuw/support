package com.youmu.cache;

import org.aopalliance.intercept.MethodInvocation;

import com.youmu.cache.annotation.Expireable;

import java.lang.reflect.Method;

/**
 * @Author: YOUMU
 * @Description: 处理过期逻辑
 * @Date: 2017/11/08
 */
public interface CacheAnnotationHandler {
    void handle(Expireable expireable, Method method);
}
