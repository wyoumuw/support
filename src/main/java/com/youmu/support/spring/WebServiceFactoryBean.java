package com.youmu.support.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/08/17
 */
public class WebServiceFactoryBean<T> implements FactoryBean<T> {
    private Class<T> webServiceInterface;

    private ServiceConfiguration serviceConfiguration;

    private T webService;

    public WebServiceFactoryBean(Class<T> webServiceInterface) {
        Assert.notNull(webServiceInterface, "webServiceInterface can not be null");
        this.webServiceInterface = webServiceInterface;
    }

    @Override
    public T getObject() throws Exception {
        webService = (T) Proxy.newProxyInstance(webServiceInterface.getClassLoader(),
                new Class[] { webServiceInterface },
                new ServiceProxy<>(webServiceInterface, serviceConfiguration));
        return webService;
    }

    @Override
    public Class<?> getObjectType() {
        return webServiceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<T> getWebServiceInterface() {
        return webServiceInterface;
    }

    public void setWebServiceInterface(Class<T> webServiceInterface) {
        this.webServiceInterface = webServiceInterface;
    }

    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }

    public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }
}
