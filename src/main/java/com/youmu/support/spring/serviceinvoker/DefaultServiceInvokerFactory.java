package com.youmu.support.spring.serviceinvoker;

import java.lang.reflect.Method;

/**
 * @Author: youmu
 * @Description:
 * @Date: 2018/08/22
 */
public class DefaultServiceInvokerFactory extends AbstractServiceInvokerFactory<DefaultServiceInvoker<?>> {
    @Override
    public DefaultServiceInvoker<?> create(Method method) {
        return new DefaultServiceInvoker<>(method, getServiceConfiguration());
    }
}
