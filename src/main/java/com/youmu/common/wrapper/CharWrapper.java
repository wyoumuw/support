package com.youmu.common.wrapper;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/13
 */
@NotThreadSafe
public class CharWrapper {
    private char target;

    public CharWrapper(char target) {
        this.target = target;
    }

    public char getTarget() {
        return target;
    }

    public void setTarget(char target) {
        this.target = target;
    }
}
