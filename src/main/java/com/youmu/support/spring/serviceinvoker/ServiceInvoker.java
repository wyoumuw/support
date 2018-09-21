package com.youmu.support.spring.serviceinvoker;

/**
 * @Author: YLBG-LDH-1506
 * @Description: 服务调用器接口
 * @Date: 2018/08/21
 */
public interface ServiceInvoker<T> {
    Object invoke(Object[] args) throws Throwable;
}
