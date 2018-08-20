package com.youmu.support.spring;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youmu.common.annotation.DependenceOn;
import com.youmu.exception.WrappedThrowable;

/**
 * Created by wyoumuw on 2018/8/17.
 */
//@DependenceOn("jackson")
public class ServiceInvoker<T> {

    private final Class<T> serviceInterface;
    private final Method method;
    private final ServiceConfiguration serviceConfiguration;
    private RequestInfoHolder requestInfoHolder;

    public ServiceInvoker(Class<T> serviceInterface, Method method,
            ServiceConfiguration serviceConfiguration) {
        this.serviceInterface = serviceInterface;
        this.method = method;
        this.serviceConfiguration = serviceConfiguration;
        init();
    }

    private void init() {
        this.requestInfoHolder = new RequestInfoHolder();
        Class controllerClass = method.getDeclaringClass();
        RequestMapping contextRequestMapping = AnnotatedElementUtils
                .getMergedAnnotation(controllerClass, RequestMapping.class);
        RequestMapping handlerRequestMapping = AnnotatedElementUtils.getMergedAnnotation(method,
                RequestMapping.class);
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
        List<SpringParamInfoHolder> paramInfos = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            createParamInfo(parameter, paramInfos);
        }
        requestInfoHolder.setUri(uriStringBuilder.toString());
        requestInfoHolder.setParamInfos(paramInfos);
        requestInfoHolder
                .setRequestMethod(0 == handlerRequestMapping.method().length ? RequestMethod.GET
                        : handlerRequestMapping.method()[0]);
    }

    /**
     * 目前不允许一个参数出现多个注解
     * @param parameter
     * @param paramInfos
     */
    protected void createParamInfo(Parameter parameter, List<SpringParamInfoHolder> paramInfos) {
        RequestParam requestParam = AnnotatedElementUtils.getMergedAnnotation(parameter,
                RequestParam.class);
        PathVariable pathVariable = AnnotatedElementUtils.getMergedAnnotation(parameter,
                PathVariable.class);
        RequestHeader requestHeader = AnnotatedElementUtils.getMergedAnnotation(parameter,
                RequestHeader.class);
        RequestBody requestBody = AnnotatedElementUtils.getMergedAnnotation(parameter,
                RequestBody.class);
        if (null != requestParam) {
            paramInfos.add(new SpringParamInfoHolder(requestParam.name(), parameter.getType(),
                    SpringParamInfoHolder.ParamType.REQUEST_PARAM));
        } else if (null != pathVariable) {
            paramInfos.add(new SpringParamInfoHolder(pathVariable.name(), parameter.getType(),
                    SpringParamInfoHolder.ParamType.PATH_VARIABLE));
        } else if (null != requestHeader) {
            paramInfos.add(new SpringParamInfoHolder(requestHeader.name(), parameter.getType(),
                    SpringParamInfoHolder.ParamType.REQUEST_PARAM));
        } else if (null != requestBody) {
            paramInfos.add(new SpringParamInfoHolder("", parameter.getType(),
                    SpringParamInfoHolder.ParamType.REQUEST_BODY));
        } else {
            paramInfos.add(new SpringParamInfoHolder("", parameter.getType(),
                    SpringParamInfoHolder.ParamType.UNKNOWN));
        }

    }

    public Object invoke(Object[] args) {
        HttpClientServiceConfiguration httpClientServiceConfiguration = (HttpClientServiceConfiguration) serviceConfiguration;
        HttpClientFactory<?> httpClientFactory = httpClientServiceConfiguration
                .getHttpClientFactory();
        CloseableHttpClient closeableHttpClient = httpClientFactory.get();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromUriString(requestInfoHolder.getUri());
        HttpUriRequest httpUriRequest = createHttpUriRequest(requestInfoHolder,
                uriComponentsBuilder, args);
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient
                    .execute(httpUriRequest);
            checkResponseStatus(closeableHttpResponse);
            Object rtn = null;
            Class rtnType = method.getReturnType();
            for (HttpMessageConverter<?> httpMessageConverter : httpClientServiceConfiguration
                    .getHttpMessageConverters()) {
                if (httpMessageConverter.canRead(rtnType, MediaType.parseMediaType(
                        closeableHttpResponse.getEntity().getContentType().getValue()))) {
                    rtn = httpMessageConverter.read(rtnType,
                            new ClientHttpResponseWrapper(closeableHttpResponse));
                    EntityUtils.consume(closeableHttpResponse.getEntity());
                    break;
                }
            }
            return rtn;
        } catch (IOException | HttpException e) {
            throw new RuntimeException(e);
        }
    }

    protected void checkResponseStatus(HttpResponse httpResponse) throws HttpException {
        if (200 < httpResponse.getStatusLine().getStatusCode()
                || 300 >= httpResponse.getStatusLine().getStatusCode()) {
            throw new HttpException();
        }
    }

    protected HttpUriRequest createHttpUriRequest(RequestInfoHolder requestInfoHolder,
            UriComponentsBuilder uriComponentsBuilder, Object[] args) {
        switch (requestInfoHolder.getRequestMethod()) {
        case GET:
            return createHttpGet(requestInfoHolder, uriComponentsBuilder, args);
        case HEAD:
            return new HttpHead(uriComponentsBuilder.build().toUri());
        case POST:
            return new HttpPost(uriComponentsBuilder.build().toUri());
        case PUT:
            return new HttpPut(uriComponentsBuilder.build().toUri());
        case PATCH:
            return new HttpPatch(uriComponentsBuilder.build().toUri());
        case DELETE:
            return new HttpDelete(uriComponentsBuilder.build().toUri());
        case OPTIONS:
            return new HttpOptions(uriComponentsBuilder.build().toUri());
        case TRACE:
            return new HttpTrace(uriComponentsBuilder.build().toUri());
        default:
            throw new IllegalArgumentException(
                    "Invalid HTTP method: " + requestInfoHolder.getRequestMethod());
        }
    }

    protected HttpGet createHttpGet(RequestInfoHolder requestInfoHolder,
            UriComponentsBuilder uriComponentsBuilder, Object[] args) {
        HttpGet httpGet = new HttpGet();
        Map<String, String> pathVariables = new HashMap<>();
        for (int i = 0; i < requestInfoHolder.getParamInfos().size(); i++) {
            SpringParamInfoHolder springParamInfoHolder = requestInfoHolder.getParamInfos().get(i);
            if (SpringParamInfoHolder.ParamType.REQUEST_PARAM == springParamInfoHolder
                    .getParamType()) {
                uriComponentsBuilder.queryParam(springParamInfoHolder.getName(),
                        serviceConfiguration.getConversionService().convert(args[i], String.class));
            } else if (SpringParamInfoHolder.ParamType.PATH_VARIABLE == springParamInfoHolder
                    .getParamType()) {
                pathVariables.put(springParamInfoHolder.getName(),
                        serviceConfiguration.getConversionService().convert(args[i], String.class));
            } else if (SpringParamInfoHolder.ParamType.REQUEST_HEADER == springParamInfoHolder
                    .getParamType()) {
                httpGet.setHeader(springParamInfoHolder.getName(),
                        serviceConfiguration.getConversionService().convert(args[i], String.class));
            } else if (SpringParamInfoHolder.ParamType.REQUEST_BODY == springParamInfoHolder
                    .getParamType()) {
                // 由于get请求不应该有body
                throw new UnsupportedOperationException("GET method cannot contains body");
            } else {
                // 处理单个model类型，如果有多个则值相同后面的会覆盖前者
                if (!isIgnoreParam(springParamInfoHolder)) {
                    Map<String, String> map = new HashMap<>();
                    transformParamObject(args[i], "", map);
                    for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                        uriComponentsBuilder.queryParam(stringStringEntry.getKey(),
                                stringStringEntry.getValue());
                    }
                }
            }
        }
        UriComponents uriComponents = uriComponentsBuilder.buildAndExpand(pathVariables);
        httpGet.setURI(uriComponents.toUri());
        return httpGet;
    }

    protected void transformParamObject(Object param, String path, Map<String, String> queryParam) {
        ConversionService conversionService = serviceConfiguration.getConversionService();
        if (null == param) {
            return;
        }
        if (conversionService.canConvert(param.getClass(), String.class)) {
            queryParam.putIfAbsent(path, conversionService.convert(param, String.class));
            return;
        } else {
            if (param instanceof Collection) {
                Iterator iterator = ((Collection) param).iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    Object next = iterator.next();
                    transformParamObject(next, path + "[" + i + "]", queryParam);
                    i++;
                }
            } else if (param instanceof Map) {
                Set<Map.Entry> entries = ((Map) param).entrySet();
                for (Map.Entry next : entries) {
                    transformParamObject(next.getValue(), path + "." + next.getKey(), queryParam);
                }
            } else {
                ReflectionUtils.doWithMethods(param.getClass(), m -> {
                    if (m.getName().startsWith("get") && 0 == m.getParameterCount()) {
                        String propertyName = getGetterPropertyName(m.getName());
                        String newPath = path + "." + propertyName;
                        if (conversionService.canConvert(m.getReturnType(), String.class)) {
                            queryParam.putIfAbsent(newPath,
                                    conversionService.convert(param, String.class));
                        } else {
                            try {
                                Object rtn = m.invoke(param);
                                transformParamObject(rtn, newPath, queryParam);
                            } catch (InvocationTargetException e) {
                                throw new WrappedThrowable(e);
                            }
                        }
                    }
                });
            }
        }
    }

    public String getGetterPropertyName(String getterName) {
        return getterName.substring(3, getterName.length());
    }

    /**
     * 去除一部分基础不需要的类型的参数化
     * @param springParamInfoHolder
     * @return
     */
    private boolean isIgnoreParam(SpringParamInfoHolder springParamInfoHolder) {
        return serviceConfiguration.getIgnoreParamClass().contains(springParamInfoHolder.getType());
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
