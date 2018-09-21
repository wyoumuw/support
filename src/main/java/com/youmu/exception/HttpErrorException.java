package com.youmu.exception;

/**
 * @Author: YLBG-LDH-1506
 * @Description: 针对HTTP的返回码出现的错误
 * @Date: 2018/09/17
 */
public class HttpErrorException extends Exception {
    private Integer code = 500;
    private String message;

    public HttpErrorException() {
    }

    public HttpErrorException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public HttpErrorException(String message) {
        super(message);
        this.message = message;
    }

    public HttpErrorException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public HttpErrorException(int code, String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    protected void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    protected void setMessage(String message) {
        this.message = message;
    }
}
