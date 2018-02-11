package com.youmu.common.wrapper;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/13
 */
@NotThreadSafe
public class FloatWrapper {
    private float target;

    public FloatWrapper(float target) {
        this.target = target;
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }
}
