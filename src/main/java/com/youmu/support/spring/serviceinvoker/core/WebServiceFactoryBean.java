package com.youmu.support.spring.serviceinvoker.core;

import java.lang.reflect.Proxy;

import com.youmu.support.spring.serviceinvoker.core.HttpServiceConfiguration;
import com.youmu.support.spring.serviceinvoker.core.ServiceInvokerFactory;
import com.youmu.support.spring.serviceinvoker.core.ServiceProxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2018/08/17
 */
public class WebServiceFactoryBean<T> implements FactoryBean<T> {
    private Class<T> webServiceInterface;

    private HttpServiceConfiguration serviceConfiguration;

    private T webService;

    private ServiceInvokerFactory serviceInvokerFactory;

    public WebServiceFactoryBean(Class<T> webServiceInterface) {
        Assert.notNull(webServiceInterface, "webServiceInterface can not be null");
        this.webServiceInterface = webServiceInterface;
    }

    @Override
    public T getObject() throws Exception {
        webService = (T) Proxy.newProxyInstance(webServiceInterface.getClassLoader(),
                new Class[] { webServiceInterface }, new ServiceProxy<>(webServiceInterface,
                        serviceInvokerFactory, serviceConfiguration));
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

    public HttpServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }

    public void setServiceConfiguration(HttpServiceConfiguration serviceConfiguration) {
        // check it
        Assert.notNull(serviceConfiguration, "serviceConfiguration cannot be null");
        serviceConfiguration.checkServiceConfiguration();
        this.serviceConfiguration = serviceConfiguration;
    }

    public void setServiceInvokerFactory(ServiceInvokerFactory serviceInvokerFactory) {
        this.serviceInvokerFactory = serviceInvokerFactory;
    }

    public ServiceInvokerFactory getServiceInvokerFactory() {
        return serviceInvokerFactory;
    }
}
