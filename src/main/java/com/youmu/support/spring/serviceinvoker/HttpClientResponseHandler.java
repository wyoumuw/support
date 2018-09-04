package com.youmu.support.spring.serviceinvoker;

import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * @Author: youmu
 * @Description:
 * @Date: 2018/08/23
 */
public interface HttpClientResponseHandler {
    /**
     * resolve response to from service value
     * @param response
     * @return
     */
    Object handleResponse(CloseableHttpResponse response, Class rtnType) throws HttpException;
}
