package com.youmu.support.spring.serviceinvoker;


import com.youmu.exception.HttpErrorException;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.lang.reflect.Type;

/**
 * @Author: YLBG-LDH-1506
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
