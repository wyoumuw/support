package com.youmu.support.spring.serviceinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by wyoumuw on 2018/8/17.
 */
// @DependenceOn("jackson")
public class DefaultServiceInvoker<T> implements ServiceInvoker<T> {

    private final static MediaType DEFAULT_MEDIATYPE = MediaType.APPLICATION_JSON;
    private final static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final Method method;
    private final HttpClientServiceConfiguration serviceConfiguration;
    private RequestInfoHolder requestInfoHolder;
    private static Pattern PATTERN_CHARSET = Pattern.compile(";\\s*charset\\s*=\\s*(\\S+)");

    public DefaultServiceInvoker(Method method, ServiceConfiguration serviceConfiguration) {
        if (!(serviceConfiguration instanceof HttpClientServiceConfiguration)) {
            throw new IllegalArgumentException(
                    "serviceConfiguration must be instance from HttpClientServiceConfiguration");
        }
        this.method = method;
        this.serviceConfiguration = (HttpClientServiceConfiguration) serviceConfiguration;
        requestInfoHolder = createRequestInfoHolder(method, serviceConfiguration);
    }

    /**
     * 提供外部扩展RequestInfoHolder
     * @param method 接口方法
     * @param serviceConfiguration 服务配置
     * @return
     */
    protected RequestInfoHolder createRequestInfoHolder(Method method,
            ServiceConfiguration serviceConfiguration) {
        RequestInfoHolder requestInfoHolder = new RequestInfoHolder();
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
        // TODO 目前只支持一个content-type
        if (0 != handlerRequestMapping.consumes().length) {
            String consume = handlerRequestMapping.consumes()[0];
            MediaType mediaType = MediaType.parseMediaType(consume);
            requestInfoHolder.setCharset(mediaType.getCharset());
            mediaType = MediaType
                    .parseMediaType(mediaType.getType() + '/' + mediaType.getSubtype());
            requestInfoHolder.setContentType(mediaType);
        }
        requestInfoHolder.setUri(uriStringBuilder.toString());
        requestInfoHolder.setParamInfos(paramInfos);
        requestInfoHolder
                .setRequestMethod(0 == handlerRequestMapping.method().length ? RequestMethod.GET
                        : handlerRequestMapping.method()[0]);
        return requestInfoHolder;
    }

    /**
     * 目前不允许一个参数出现多个注解，可以自己扩展
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
                    SpringParamInfoHolder.ParamType.REQUEST_HEADER));
        } else if (null != requestBody) {
            paramInfos.add(new SpringParamInfoHolder("", parameter.getType(),
                    SpringParamInfoHolder.ParamType.REQUEST_BODY));
        } else {
            paramInfos.add(new SpringParamInfoHolder("", parameter.getType(),
                    SpringParamInfoHolder.ParamType.UNKNOWN));
        }

    }

    @Override
    public Object invoke(Object[] args) throws Throwable {
        HttpClientServiceConfiguration httpClientServiceConfiguration = serviceConfiguration;
        HttpClientFactory<?> httpClientFactory = httpClientServiceConfiguration
                .getHttpClientFactory();
        CloseableHttpClient closeableHttpClient = httpClientFactory.get();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromUriString(requestInfoHolder.getUri());
        HttpUriRequest httpUriRequest = createHttpUriRequest(requestInfoHolder,
                uriComponentsBuilder, args);
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpUriRequest);
        return httpClientServiceConfiguration.getHttpClientResponseHandler()
                .handleResponse(closeableHttpResponse, method.getGenericReturnType());
    }

    /**
     * 创建HTTPClient的request
     * @param requestInfoHolder request的信息
     * @param uriComponentsBuilder uri
     * @param args 调用服务的时候的入参
     * @return
     */
    protected HttpUriRequest createHttpUriRequest(RequestInfoHolder requestInfoHolder,
            UriComponentsBuilder uriComponentsBuilder, Object[] args) {
        switch (requestInfoHolder.getRequestMethod()) {
        case GET:
            return createNoBodyRequest(requestInfoHolder, uriComponentsBuilder, args);
        // case HEAD:
        // return new HttpHead(uriComponentsBuilder.build().toUri());
        case POST:
            return createBodyRequest(requestInfoHolder, uriComponentsBuilder, args);
        case PUT:
            return createBodyRequest(requestInfoHolder, uriComponentsBuilder, args);
        // case PATCH:
        // return new HttpPatch(uriComponentsBuilder.build().toUri());
        case DELETE:
            return createNoBodyRequest(requestInfoHolder, uriComponentsBuilder, args);
        case OPTIONS:
            return createNoBodyRequest(requestInfoHolder, uriComponentsBuilder, args);
        // case TRACE:
        // return new HttpTrace(uriComponentsBuilder.build().toUri());
        default:
            throw new IllegalArgumentException(
                    "Invalid HTTP method: " + requestInfoHolder.getRequestMethod());
        }
    }

    protected HttpRequestBase createNoBodyRequest(RequestInfoHolder requestInfoHolder,
            UriComponentsBuilder uriComponentsBuilder, Object[] args) {
        HttpRequestBase httpRequest;
        if (requestInfoHolder.getRequestMethod() == RequestMethod.DELETE) {
            httpRequest = new HttpDelete();
        } else if (requestInfoHolder.getRequestMethod() == RequestMethod.GET) {
            httpRequest = new HttpGet();
        } else if (requestInfoHolder.getRequestMethod() == RequestMethod.OPTIONS) {
            httpRequest = new HttpOptions();
        } else {
            throw new UnsupportedOperationException("Request type is not supported");
        }
        // set default headers
        setDefaultHeaders(httpRequest);
        Map<String, String> pathVariables = new HashMap<>();
        for (int i = 0; i < requestInfoHolder.getParamInfos().size(); i++) {
            SpringParamInfoHolder springParamInfoHolder = requestInfoHolder.getParamInfos().get(i);
            if (SpringParamInfoHolder.ParamType.REQUEST_PARAM == springParamInfoHolder
                    .getParamType()) {
                setQueryParam(uriComponentsBuilder, springParamInfoHolder.getName(),
                        convertStringOrNull(args[i], springParamInfoHolder.getType()));
            } else if (SpringParamInfoHolder.ParamType.PATH_VARIABLE == springParamInfoHolder
                    .getParamType()) {
                pathVariables.put(springParamInfoHolder.getName(),
                        convertStringOrNull(args[i], springParamInfoHolder.getType()));
            } else if (SpringParamInfoHolder.ParamType.REQUEST_HEADER == springParamInfoHolder
                    .getParamType()) {
                setHeaderParam(httpRequest, springParamInfoHolder.getName(),
                        convertStringOrNull(args[i], springParamInfoHolder.getType()));
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
                        setQueryParam(uriComponentsBuilder, stringStringEntry.getKey(),
                                stringStringEntry.getValue());
                    }
                }
            }
        }
        UriComponents uriComponents = uriComponentsBuilder.buildAndExpand(pathVariables);
        httpRequest.setURI(uriComponents.toUri());
        return httpRequest;
    }

    protected HttpEntityEnclosingRequestBase createBodyRequest(RequestInfoHolder requestInfoHolder,
            UriComponentsBuilder uriComponentsBuilder, Object[] args) {
        HttpEntityEnclosingRequestBase httpRequest;
        if (requestInfoHolder.getRequestMethod() == RequestMethod.PUT) {
            httpRequest = new HttpPut();
        } else if (requestInfoHolder.getRequestMethod() == RequestMethod.POST) {
            httpRequest = new HttpPost();
        } else {
            throw new UnsupportedOperationException("Request is not supported");
        }
        // set default headers
        setDefaultHeaders(httpRequest);
        Map<String, String> pathVariables = new HashMap<>();
        HttpEntity httpEntity = null;
        for (int i = 0; i < requestInfoHolder.getParamInfos().size(); i++) {
            SpringParamInfoHolder springParamInfoHolder = requestInfoHolder.getParamInfos().get(i);
            if (SpringParamInfoHolder.ParamType.REQUEST_PARAM == springParamInfoHolder
                    .getParamType()) {
                setQueryParam(uriComponentsBuilder, springParamInfoHolder.getName(),
                        convertStringOrNull(args[i], springParamInfoHolder.getType()));
            } else if (SpringParamInfoHolder.ParamType.PATH_VARIABLE == springParamInfoHolder
                    .getParamType()) {
                pathVariables.put(springParamInfoHolder.getName(),
                        convertStringOrNull(args[i], springParamInfoHolder.getType()));
            } else if (SpringParamInfoHolder.ParamType.REQUEST_HEADER == springParamInfoHolder
                    .getParamType()) {
                setHeaderParam(httpRequest, springParamInfoHolder.getName(),
                        convertStringOrNull(args[i], springParamInfoHolder.getType()));
            } else if (SpringParamInfoHolder.ParamType.REQUEST_BODY == springParamInfoHolder
                    .getParamType()) {
                if (null == httpEntity) {
                    httpEntity = paramToEntity(springParamInfoHolder.getType(), args[i],
                            requestInfoHolder.getContentType());
                } else {
                    throw new IllegalArgumentException("cannot contains @RequestBody more than 2!");
                }
            } else {
                // 处理单个model类型，如果有多个则值相同后面的会覆盖前者
                if (!isIgnoreParam(springParamInfoHolder)) {
                    if (null == httpEntity) {
                        httpEntity = paramToEntity(springParamInfoHolder.getType(), args[i],
                                requestInfoHolder.getContentType());
                    } else {
                        throw new IllegalArgumentException(
                                "cannot contains Body Object more than 2! Is it IgnoreParam in serviceConfiguration.");
                    }
                }
            }
        }
        UriComponents uriComponents = uriComponentsBuilder.buildAndExpand(pathVariables);
        httpRequest.setURI(uriComponents.toUri());
        httpRequest.setEntity(httpEntity);
        return httpRequest;
    }

    /**
     * 添加默认的头
     * @param request
     */
    private void setDefaultHeaders(HttpRequest request) {
        if (!CollectionUtils.isEmpty(serviceConfiguration.getHeaders())) {
            for (Map.Entry<String, String> entry : serviceConfiguration.getHeaders().entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private void setQueryParam(UriComponentsBuilder uriComponentsBuilder, String name,
            String value) {
        if (null != value) {
            uriComponentsBuilder.queryParam(name, value);
        }
    }

    private void setHeaderParam(HttpRequest request, String name, String value) {
        if (null != value) {
            request.setHeader(name, value);
        }
    }

    /**
     * 把对象平铺到map， object as json: <code>
     * {
     * "name": "youmu",
     * "place":{
     * "name": "touhou",
     * },
     * "relations":[{
     * "name":"xxx"
     * }
     * ]
     * }
     * </code> the result by this method : <code>
     * {
     * "name":"youmu"，
     * "place.name":"touhou",
     * "relations[0].name":"xxx"
     * }
     * </code> all value will transform to String by
     * ServiceConfiguration.ConversionService
     * @param param
     * @param path
     * @param queryParam
     */
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
                    // 排除Object的方法
                    if (Object.class.equals(m.getDeclaringClass())) {
                        return;
                    }
                    // 处理所有getter
                    if (m.getName().startsWith("get") && 0 == m.getParameterCount()) {
                        String propertyName = getGetterPropertyName(m.getName());
                        String newPath = (StringUtils.hasText(path) ? path + "." : path)
                                + propertyName;
                        Object rtn = ReflectionUtils.invokeMethod(m, param);
                        if (conversionService.canConvert(m.getReturnType(), String.class)) {
                            queryParam.putIfAbsent(newPath,
                                    conversionService.convert(rtn, String.class));
                        } else {
                            transformParamObject(rtn, newPath, queryParam);
                        }
                    }
                });
            }
        }
    }

    /**
     * 根据getter方法获取其属性名
     * @param getterName
     * @return
     */
    private String getGetterPropertyName(String getterName) {
        return StringUtils.uncapitalize(getterName.substring(3, getterName.length()));
    }

    /**
     * 去除一部分基础不需要的类型的参数化
     * @param springParamInfoHolder 参数信息
     * @return
     */
    protected boolean isIgnoreParam(SpringParamInfoHolder springParamInfoHolder) {
        if (CollectionUtils.isEmpty(serviceConfiguration.getIgnoredParamClasses())) {
            return false;
        }
        for (Class ignoredClass : serviceConfiguration.getIgnoredParamClasses()) {
            if (ignoredClass.isAssignableFrom(springParamInfoHolder.getType())) {
                return true;
            }
        }
        return false;
    }

    protected String convertStringOrNull(Object source, Class sourceClass) {
        ConversionService conversionService = serviceConfiguration.getConversionService();
        if (!conversionService.canConvert(sourceClass, String.class)) {
            return null;
        }
        return conversionService.convert(source, String.class);
    }

    protected HttpEntity paramToEntity(Class<?> type, Object values, MediaType contentType) {
        // MediaType contentType = MediaType.APPLICATION_JSON;
        ByteArrayEntityMessage byteArrayEntityMessage = new ByteArrayEntityMessage(contentType,
                requestInfoHolder.getCharset());
        for (HttpMessageConverter httpMessageConverter : serviceConfiguration
                .getHttpMessageConverters()) {
            if (httpMessageConverter.canWrite(type, contentType)) {
                try {
                    httpMessageConverter.write(values, contentType, byteArrayEntityMessage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return byteArrayEntityMessage.getEntity();
    }

    static class ByteArrayEntityMessage implements HttpOutputMessage {

        private ByteArrayOutputStream stream;

        private MediaType contentType;
        private Charset charset;

        ByteArrayEntityMessage(MediaType contentType, Charset charset) {
            this.contentType = null == contentType ? DEFAULT_MEDIATYPE : contentType;
            this.charset = null == charset ? DEFAULT_CHARSET : charset;
        }

        @Override
        public OutputStream getBody() throws IOException {
            if (null == stream) {
                stream = new ByteArrayOutputStream();
            }
            return stream;
        }

        @Override
        public HttpHeaders getHeaders() {
            return new HttpHeaders();
        }

        public HttpEntity getEntity() {
            if (null == stream) {
                return new StringEntity("", charset);
            }
            return new ByteArrayEntity(stream.toByteArray(),
                    ContentType.create(contentType.toString()));
        }
    }
}
