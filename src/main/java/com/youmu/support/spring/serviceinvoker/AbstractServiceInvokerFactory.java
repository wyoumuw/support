package com.youmu.support.spring.serviceinvoker;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/08/22
 */
public abstract class AbstractServiceInvokerFactory<T extends ServiceInvoker<?>>
        implements ServiceInvokerFactory<T> {

    protected ServiceConfiguration serviceConfiguration;

    @Override
    public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }
}
