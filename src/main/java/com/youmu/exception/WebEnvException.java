package com.youmu.exception;

/**
 * @Author: YOUMU
 * @Description: Web环境的exception
 * @Date: 2018/09/13
 */
public class WebEnvException extends RuntimeException {
    public WebEnvException() {
    }

    public WebEnvException(String message) {
        super(message);
    }

    public WebEnvException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebEnvException(Throwable cause) {
        super(cause);
    }

    public WebEnvException(String message, Throwable cause, boolean enableSuppression,
						   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
