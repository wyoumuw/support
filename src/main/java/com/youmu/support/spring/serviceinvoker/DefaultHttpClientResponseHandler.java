package com.youmu.support.spring.serviceinvoker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youmu.exception.HttpErrorException;
import com.youmu.exception.WrappedThrowable;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @Author: YLBG-LDH-1506
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
    public Object handleResponse(CloseableHttpResponse response, Type rtnType)
            throws HttpErrorException {
        checkResponseStatus(response);
        Object rtn = null;
        // return null if no content
        if (0 == response.getEntity().getContentLength()) {
            return null;
        }
        // get contentTyp
        MediaType contentType = null == response.getEntity().getContentType() ? null
                : MediaType.parseMediaType(response.getEntity().getContentType().getValue());
        // foreach all HttpMessageConverter from
        // httpClientServiceConfiguration.getHttpMessageConverters()
        for (HttpMessageConverter<?> httpMessageConverter : httpClientServiceConfiguration
                .getHttpMessageConverters()) {
            // method return a normal type that instanceof Class as method "Person get()";
            if (rtnType instanceof Class) {
                Class rtnClass = (Class) rtnType;
                // pending can convert the request body to rtnClass
                if (httpMessageConverter.canRead(rtnClass, contentType)) {
                    try {
                        // just read it
                        rtn = httpMessageConverter.read(rtnClass,
                                new ClientHttpResponseWrapper(response));
                        EntityUtils.consume(response.getEntity());
                    } catch (IOException e) {
                        throw new WrappedThrowable(new IOException("consume entity", e));
                    }
                    break;
                }
            } else if (rtnType instanceof ParameterizedType) {
                // or the method return a generic type as "Map<String,Person> get()"
                if (httpMessageConverter instanceof GenericHttpMessageConverter
                        && ((GenericHttpMessageConverter<?>) httpMessageConverter).canRead(rtnType,
                                null, contentType)) {
                    try {
                        rtn = ((GenericHttpMessageConverter<?>) httpMessageConverter).read(rtnType,
                                null, new ClientHttpResponseWrapper(response));
                        EntityUtils.consume(response.getEntity());
                    } catch (IOException e) {
                        throw new WrappedThrowable(new IOException("consume entity", e));
                    }
                    break;
                } else {
                    continue;
                }
            } else {
                throw new WrappedThrowable(new IOException(
                        "unable read contentType " + contentType + " to " + rtnType));
            }
        }
        return rtn;
    }

    /**
     * 检测response的status,目前对springboot的服务进行默认处理。优先吧body转成jsonobject然后取其message来作为异常信息
     * @param httpResponse
     * @throws HttpErrorException
     */
    protected void checkResponseStatus(CloseableHttpResponse httpResponse)
            throws HttpErrorException {
        if (200 < httpResponse.getStatusLine().getStatusCode()
                || 300 <= httpResponse.getStatusLine().getStatusCode()) {
            // 出错了尝试解析body
            try {
                String entity = EntityUtils.toString(httpResponse.getEntity());
                Map map = new ObjectMapper().readValue(entity, Map.class);
                // 尝试获取message字段
                throw new HttpErrorException(httpResponse.getStatusLine().getStatusCode(),
                        (String) map.get("message"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 无法获取尝试从http头获取
            if (StringUtils.hasText(httpResponse.getStatusLine().getReasonPhrase())) {
                throw new HttpErrorException(httpResponse.getStatusLine().getStatusCode(),
                        httpResponse.getStatusLine().getReasonPhrase());
            }
            // 头信息也为空，返回未知错误
            throw new HttpErrorException(httpResponse.getStatusLine().getStatusCode(), "服务调用错误");
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
