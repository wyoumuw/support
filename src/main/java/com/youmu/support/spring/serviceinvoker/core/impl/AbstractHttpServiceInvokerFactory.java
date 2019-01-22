package com.youmu.support.spring.serviceinvoker.core.impl;

import com.youmu.support.spring.serviceinvoker.core.HttpServiceConfiguration;
import com.youmu.support.spring.serviceinvoker.core.ServiceInvoker;
import com.youmu.support.spring.serviceinvoker.core.ServiceInvokerFactory;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2018/08/22
 */
public abstract class AbstractHttpServiceInvokerFactory<T extends ServiceInvoker<?>>
        implements ServiceInvokerFactory<T> {

    protected HttpServiceConfiguration serviceConfiguration;

    @Override
    public void setServiceConfiguration(HttpServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    public HttpServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }
}
