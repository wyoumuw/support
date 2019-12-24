package com.youmu.support.spring.serviceinvoker.core;

import java.lang.reflect.Method;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2018/08/21
 */
public interface ServiceInvokerFactory<T extends ServiceInvoker<?>> {

    // 用虾面这个个方法来确定invoker的环境
    void setServiceConfiguration(HttpServiceConfiguration serviceConfiguration);

    /**
     * 根据method方法来创建一个ServiceInvoker
     * @param method 接口的方法
     * @return serviceInvoker
     */
    T create(Method method);
}
