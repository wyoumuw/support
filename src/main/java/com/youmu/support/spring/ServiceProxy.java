package com.youmu.support.spring;

import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wyoumuw on 2018/8/16.
 */
public class ServiceProxy<T> implements InvocationHandler {

    private Map<Method, ServiceInvoker> cachedInvokers = new ConcurrentHashMap<>();

    private Class<T> serviceInterface;

    private ServiceConfiguration serviceConfiguration;

    public ServiceProxy(Class<T> serviceInterface, ServiceConfiguration serviceConfiguration) {
        Assert.notNull(serviceInterface, "serviceInterface cannot be null");
        Assert.notNull(serviceConfiguration, "serviceConfiguration cannot be null");
        this.serviceInterface = serviceInterface;
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return getInvoker(method).invoke(args);
    }

    public ServiceInvoker getInvoker(Method method) {
        ServiceInvoker serviceInvoker = cachedInvokers.get(method);
        if (null == serviceInvoker) {
            serviceInvoker = new ServiceInvoker(serviceInterface, method, serviceConfiguration);
        }
        return serviceInvoker;
    }

    public Class<T> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }

    public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }
}
