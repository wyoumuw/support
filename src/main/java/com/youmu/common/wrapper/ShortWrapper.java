package com.youmu.common.wrapper;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/13
 */
@NotThreadSafe
public class ShortWrapper {
    private short target;

    public ShortWrapper(short target) {
        this.target = target;
    }

    public short getTarget() {
        return target;
    }

    public void setTarget(short target) {
        this.target = target;
    }
}
