package com.youmu.support.spring.serviceinvoker.httpclient.impl;

import com.youmu.support.spring.serviceinvoker.core.impl.AbstractHttpServiceInvokerFactory;

import java.lang.reflect.Method;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2018/08/22
 */
public class DefaultServiceInvokerFactory extends AbstractHttpServiceInvokerFactory<DefaultServiceInvoker<?>> {
    @Override
    public DefaultServiceInvoker<?> create(Method method) {
        return new DefaultServiceInvoker<>(method, getServiceConfiguration());
    }
}
