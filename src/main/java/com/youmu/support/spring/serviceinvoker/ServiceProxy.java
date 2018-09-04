package com.youmu.support.spring.serviceinvoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

/**
 * Created by wyoumuw on 2018/8/16.
 */
public class ServiceProxy<T> implements InvocationHandler {

    private Map<Method, DefaultServiceInvoker> cachedInvokers = new ConcurrentHashMap<>();

    private Class<T> serviceInterface;

    private ServiceConfiguration serviceConfiguration;

    private ServiceInvokerFactory serviceInvokerFactory;

    public ServiceProxy(Class<T> serviceInterface, ServiceInvokerFactory serviceInvokerFactory,
            ServiceConfiguration serviceConfiguration) {
        Assert.notNull(serviceInterface, "serviceInterface cannot be null");
        Assert.notNull(serviceInvokerFactory, "serviceInvokerFactory cannot be null");
        Assert.notNull(serviceConfiguration, "serviceConfiguration cannot be null");
        this.serviceInterface = serviceInterface;
        this.serviceInvokerFactory = serviceInvokerFactory;
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        // Unsupported default method
        // else if (isDefaultMethod(method)) {
        // return invokeDefaultMethod(proxy, method, args);
        // }
        return getInvoker(method).invoke(args);
    }

    private ServiceInvoker getInvoker(Method method) {
        ServiceInvoker serviceInvoker = cachedInvokers.get(method);
        if (null == serviceInvoker) {
            serviceInvoker = serviceInvokerFactory.create(method);
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
