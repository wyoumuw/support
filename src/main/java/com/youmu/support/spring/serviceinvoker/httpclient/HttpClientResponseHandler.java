package com.youmu.support.spring.serviceinvoker.httpclient;

import java.lang.reflect.Type;

import com.youmu.exception.HttpErrorException;
import org.apache.http.client.methods.CloseableHttpResponse;


/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2018/08/23
 */
public interface HttpClientResponseHandler {
    /**
     * resolve response to from service value
     * @param response
     * @return
     */
    Object handleResponse(CloseableHttpResponse response, Type rtnType) throws HttpErrorException;
}
