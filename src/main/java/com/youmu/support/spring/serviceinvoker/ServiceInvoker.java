package com.youmu.support.spring.serviceinvoker;

/**
 * @Author: youmu
 * @Description: 服务调用器接口
 * @Date: 2018/08/21
 */
public interface ServiceInvoker<T> {
    Object invoke(Object[] args);
}
