package com.youmu.exception;

/**
 * @Author: YOUMU
 * @Description: 不支持的目标
 * @Date: 2017/08/09
 */
public class UnsupportedTargetException extends RuntimeException {

    public UnsupportedTargetException() {
    }

    public UnsupportedTargetException(String message) {
        super(message);
    }

    public UnsupportedTargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedTargetException(Throwable cause) {
        super(cause);
    }

    public UnsupportedTargetException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
