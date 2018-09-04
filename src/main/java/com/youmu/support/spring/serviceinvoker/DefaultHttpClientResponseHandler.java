package com.youmu.support.spring.serviceinvoker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;

/**
 * @Author: youmu
 * @Description:
 * @Date: 2018/08/23
 */
public class DefaultHttpClientResponseHandler implements HttpClientResponseHandler {

    private HttpClientServiceConfiguration httpClientServiceConfiguration;

    public DefaultHttpClientResponseHandler(
            HttpClientServiceConfiguration httpClientServiceConfiguration) {
        Assert.notNull(httpClientServiceConfiguration,
                "httpClientServiceConfiguration cannot be null");
        this.httpClientServiceConfiguration = httpClientServiceConfiguration;
    }

    @Override
    public Object handleResponse(CloseableHttpResponse response, Class rtnType)
            throws HttpException {
        if (404 == response.getStatusLine().getStatusCode()) {
            return httpClientServiceConfiguration.getConversionService().convert(null, rtnType);
        }
        checkResponseStatus(response);
        Object rtn = null;
        // return null if no content
        if (0 == response.getEntity().getContentLength()) {
            return null;
        }
        for (HttpMessageConverter<?> httpMessageConverter : httpClientServiceConfiguration
                .getHttpMessageConverters()) {
            MediaType contentType = null == response.getEntity().getContentType() ? null
                    : MediaType.parseMediaType(response.getEntity().getContentType().getValue());
            if (httpMessageConverter.canRead(rtnType, contentType)) {
                try {
                    rtn = httpMessageConverter.read(rtnType,
                            new ClientHttpResponseWrapper(response));
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    throw new HttpException("consume entity", e);
                }
                break;
            }
        }
        return rtn;
    }

    /**
     * 检测response的status
     * @param httpResponse
     * @throws HttpException
     */
    protected void checkResponseStatus(CloseableHttpResponse httpResponse) throws HttpException {
        if (401 == httpResponse.getStatusLine().getStatusCode()) {
            throw new AuthenticationException(httpResponse.getStatusLine().getReasonPhrase());
        }
        if (200 < httpResponse.getStatusLine().getStatusCode()
                || 300 <= httpResponse.getStatusLine().getStatusCode()) {
            throw new HttpException("error response:" + httpResponse.getStatusLine().getStatusCode()
                    + " " + httpResponse.getStatusLine().getReasonPhrase());
        }
    }

    static class ClientHttpResponseWrapper implements HttpInputMessage {

        private final CloseableHttpResponse response;

        private byte[] body;

        private HttpHeaders httpHeaders;

        ClientHttpResponseWrapper(CloseableHttpResponse response) {
            this.response = response;
        }

        @Override
        public HttpHeaders getHeaders() {
            if (this.httpHeaders == null) {
                this.httpHeaders = new HttpHeaders();
                for (Header header : response.getAllHeaders()) {
                    this.httpHeaders.add(header.getName(), header.getValue());
                }
            }
            return httpHeaders;
        }

        @Override
        public InputStream getBody() throws IOException {
            if (this.body == null) {
                this.body = EntityUtils.toByteArray(response.getEntity());
            }
            return new ByteArrayInputStream(this.body);
        }
    }
}
