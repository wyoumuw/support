package com.youmu.common.wrapper;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/13
 */
public class Wrapper<T> {

    private T target;

    public Wrapper(T target) {
        this.target = target;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }
}
