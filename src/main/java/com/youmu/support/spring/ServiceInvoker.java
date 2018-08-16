package com.youmu.support.spring;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class ServiceInvoker<T> {

    private final Class<T> serviceInterface;
    private final Method method;
    private final ServiceConfiguration serviceConfiguration;
    private RequestInfoHolder requestInfoHolder;

    public ServiceInvoker(Class<T> serviceInterface, Method method, ServiceConfiguration serviceConfiguration) {
        this.serviceInterface = serviceInterface;
        this.method = method;
        this.serviceConfiguration = serviceConfiguration;
        init();
    }

    private void init() {
        this.requestInfoHolder = new RequestInfoHolder();
        Class controllerClass = method.getDeclaringClass();
        RequestMapping contextRequestMapping = AnnotatedElementUtils.getMergedAnnotation(controllerClass, RequestMapping.class);
        RequestMapping handlerRequestMapping = AnnotatedElementUtils.getMergedAnnotation(method, RequestMapping.class);
        String standardServer = serviceConfiguration.getServer();
        StringBuilder uriStringBuilder = new StringBuilder(standardServer);
        if (0 == contextRequestMapping.path().length) {
            uriStringBuilder.append("/");
        } else {
            uriStringBuilder.append(contextRequestMapping.path()[0]);
        }
        if (0 != handlerRequestMapping.path().length) {
            uriStringBuilder.append(handlerRequestMapping.path()[0]);
        }
        List<String> paramNames = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            paramNames.add(parameter.getName());
        }
        requestInfoHolder.setUri(uriStringBuilder.toString());
        requestInfoHolder.setParamNames(paramNames);
        requestInfoHolder.setRequestMethod(0 == handlerRequestMapping.method().length ? RequestMethod.GET : handlerRequestMapping.method()[0]);
    }

    public Object invoke(Object[] args) {
        SpringHttpClientServiceConfiguration springHttpClientServiceConfiguration = (SpringHttpClientServiceConfiguration) serviceConfiguration;
        HttpClientFactory<?> httpClientFactory = springHttpClientServiceConfiguration.getHttpClientFactory();
        CloseableHttpClient closeableHttpClient = httpClientFactory.get();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(requestInfoHolder.getUri());
        ConversionService conversionService = springHttpClientServiceConfiguration.getConversionService();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            List<String> paramNames = requestInfoHolder.getParamNames();
            uriComponentsBuilder.queryParam(paramNames.get(i), conversionService.convert(arg, String.class));
        }
        HttpUriRequest httpUriRequest = createHttpUriRequest(requestInfoHolder.getRequestMethod(), uriComponentsBuilder.build().toUri());
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpUriRequest);
            checkResponseStatus(closeableHttpResponse);
            Object rtn = null;
            Class rtnType = method.getReturnType();
            for (HttpMessageConverter<?> httpMessageConverter : springHttpClientServiceConfiguration.getHttpMessageConverters()) {
                if (httpMessageConverter.canRead(rtnType, MediaType.parseMediaType(closeableHttpResponse.getEntity().getContentType().getValue()))) {
                    rtn = httpMessageConverter.read(rtnType, new ClientHttpResponseWrapper(closeableHttpResponse));
                    break;
                }
            }
            return rtn;
        } catch (IOException | HttpException e) {
            throw new RuntimeException(e);
        }
    }

    protected void checkResponseStatus(HttpResponse httpResponse) throws HttpException {
        if (200 < httpResponse.getStatusLine().getStatusCode() || 300 >= httpResponse.getStatusLine().getStatusCode()) {
            throw new HttpException();
        }
    }

    protected HttpUriRequest createHttpUriRequest(RequestMethod requestMethod, URI uri) {
        switch (requestMethod) {
            case GET:
                return new HttpGet(uri);
            case HEAD:
                return new HttpHead(uri);
            case POST:
                return new HttpPost(uri);
            case PUT:
                return new HttpPut(uri);
            case PATCH:
                return new HttpPatch(uri);
            case DELETE:
                return new HttpDelete(uri);
            case OPTIONS:
                return new HttpOptions(uri);
            case TRACE:
                return new HttpTrace(uri);
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + requestMethod);
        }
    }

    static class ClientHttpResponseWrapper implements HttpInputMessage, Closeable {

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

        @Override
        public void close() {
            try {
                this.response.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
