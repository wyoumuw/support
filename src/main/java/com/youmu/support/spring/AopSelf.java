package com.youmu.support.spring;

import org.springframework.aop.framework.AopContext;

/**
 * 针对aop的exposeProxy设置true的情况，可以使用此方法调用自身方法并且能走AOP
 * @author YOUMU
 * @version V1.0
 * @since 2019-12-24 17:05
 */
public interface AopSelf<T> {

    @SuppressWarnings("unchecked")
    default T self() {
        return (T) AopContext.currentProxy();
    }
}