package com.youmu.support.spring;

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

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return cachedInvokers.get(method).invoke(args);
    }

    public ServiceInvoker getInvoker(Method method) {
        ServiceInvoker serviceInvoker = cachedInvokers.get(method);
        if (null == serviceInvoker) {
            serviceInvoker = new ServiceInvoker(serviceInterface, method, serviceConfiguration);
        }
        return serviceInvoker;
    }
}
