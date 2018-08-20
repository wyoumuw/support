package com.youmu.exception;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/08/17
 */
public class WrappedThrowable extends RuntimeException {
    public WrappedThrowable() {
    }

    public WrappedThrowable(String message) {
        super(message);
    }

    public WrappedThrowable(String message, Throwable cause) {
        super(message, cause);
    }

    public WrappedThrowable(Throwable cause) {
        super(cause);
    }

    public WrappedThrowable(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
