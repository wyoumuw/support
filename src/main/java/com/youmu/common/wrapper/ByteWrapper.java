package com.youmu.common.wrapper;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/13
 */
@NotThreadSafe
public class ByteWrapper {
    private byte target;

    public ByteWrapper(byte target) {
        this.target = target;
    }

    public byte getTarget() {
        return target;
    }

    public void setTarget(byte target) {
        this.target = target;
    }
}
