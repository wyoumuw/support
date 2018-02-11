package com.youmu.common.wrapper;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/13
 */
@NotThreadSafe
public class BooleanWrapper {
    private boolean target;

    public BooleanWrapper(boolean target) {
        this.target = target;
    }

    public boolean getTarget() {
        return target;
    }

    public void setTarget(boolean target) {
        this.target = target;
    }
}
