package com.youmu.support.spring.web;

import java.lang.reflect.Method;
import java.util.List;

import com.youmu.support.spring.serviceinvoker.FilterFeignClientRequestMappingHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;

/**
 * @Author: YLBG-YCY-1325
 * @Description: 让spring mvc 支持接口注解
 * @Date: 2018/5/4 11:54
 */
public class MvcSupportConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ConfigurableBeanFactory beanFactory;

    @Autowired
    private List<HttpMessageConverter<?>> httpMessageConverters;

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new FilterFeignClientRequestMappingHandlerMapping();
    }

    /**
     * 让spring mvc支持接口上的注解
     * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#getDefaultArgumentResolvers()}
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestParamMethodArgumentResolver(beanFactory, false) {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return super.supportsParameter(
                        interfaceMethodParameter(parameter, RequestParam.class));
            }

            @Override
            protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
                return super.createNamedValueInfo(
                        interfaceMethodParameter(parameter, RequestParam.class));
            }
        });
        resolvers.add(new RequestParamMapMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return super.supportsParameter(
                        interfaceMethodParameter(parameter, RequestParam.class));
            }
        });
        resolvers.add(new PathVariableMethodArgumentResolver() { // PathVariable 支持接口注解
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return super.supportsParameter(
                        interfaceMethodParameter(parameter, PathVariable.class));
            }

            @Override
            protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
                return super.createNamedValueInfo(
                        interfaceMethodParameter(parameter, PathVariable.class));
            }
        });
        resolvers.add(new RequestHeaderMethodArgumentResolver(beanFactory) { // RequestHeader
                                                                             // 支持接口注解
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return super.supportsParameter(
                        interfaceMethodParameter(parameter, RequestHeader.class));
            }

            @Override
            protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
                return super.createNamedValueInfo(
                        interfaceMethodParameter(parameter, RequestHeader.class));
            }
        });

        resolvers.add(new ServletCookieValueMethodArgumentResolver(beanFactory) { // CookieValue
                                                                                  // 支持接口注解
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return super.supportsParameter(
                        interfaceMethodParameter(parameter, CookieValue.class));
            }

            @Override
            protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
                return super.createNamedValueInfo(
                        interfaceMethodParameter(parameter, CookieValue.class));
            }
        });

        resolvers.add(new RequestResponseBodyMethodProcessor(httpMessageConverters) { // RequestBody
                                                                                      // 支持接口注解
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return super.supportsParameter(
                        interfaceMethodParameter(parameter, RequestBody.class));
            }

            /**
             * 不支持此方法
             * @param binder
             * @param methodParam
             */
            @Override
            protected void validateIfApplicable(WebDataBinder binder, MethodParameter methodParam) { // 支持@Valid验证
                super.validateIfApplicable(binder, methodParam);
            }
        });
    }

    public static MethodParameter interfaceMethodParameter(MethodParameter parameter,
            Class annotationType) {
        if (!parameter.hasParameterAnnotation(annotationType)) {
            for (Class<?> itfc : parameter.getDeclaringClass().getInterfaces()) {
                try {
                    Method method = itfc.getMethod(parameter.getMethod().getName(),
                            parameter.getMethod().getParameterTypes());
                    MethodParameter itfParameter = new SynthesizingMethodParameter(method,
                            parameter.getParameterIndex());
                    if (itfParameter.hasParameterAnnotation(annotationType)) {
                        return itfParameter;
                    }
                } catch (NoSuchMethodException e) {
                    continue;
                }
            }
        }
        return parameter;
    }
}
