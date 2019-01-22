package com.youmu.support.spring.serviceinvoker.core;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.transform.Source;

import com.youmu.support.spring.serviceinvoker.protocol.ServiceDateConverter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;

/**
 * Created by wyoumuw on 2018/8/17.
 */
public class HttpServiceConfiguration {

    private static boolean romePresent = ClassUtils.isPresent("com.rometools.rome.feed.WireFeed",
            RestTemplate.class.getClassLoader());

    private static final boolean jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder",
            RestTemplate.class.getClassLoader());

    private static final boolean jackson2Present = ClassUtils.isPresent(
            "com.fasterxml.jackson.databind.ObjectMapper", RestTemplate.class.getClassLoader())
            && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
                    RestTemplate.class.getClassLoader());

    private static final boolean jackson2XmlPresent = ClassUtils.isPresent(
            "com.fasterxml.jackson.dataformat.xml.XmlMapper", RestTemplate.class.getClassLoader());

    private static final boolean gsonPresent = ClassUtils.isPresent("com.google.gson.Gson",
            RestTemplate.class.getClassLoader());

    private String server;

    private ConversionService conversionService;

    private List<HttpMessageConverter<?>> httpMessageConverters;

    private List<Class> ignoredParamClasses;

    /**
     * 初始化，会初始化所有值成默认值
     */
    {
        conversionService = defaultConversionService();
        httpMessageConverters = defaultHttpMessageConverters();
        ignoredParamClasses = defaultIgnoreParamClasses();
    }

    protected List<Class> defaultIgnoreParamClasses() {
        return Lists.newArrayList(Model.class, ServletRequest.class, ServletResponse.class);
    }

    protected List<HttpMessageConverter<?>> defaultHttpMessageConverters() {
        List<HttpMessageConverter<?>> list = Lists.newArrayList();
        if (jackson2Present) {
            list.add(new MappingJackson2HttpMessageConverter());
        } else if (gsonPresent) {
            list.add(new GsonHttpMessageConverter());
        }
        list.add(new ByteArrayHttpMessageConverter());
        list.add(new StringHttpMessageConverter());
        list.add(new ResourceHttpMessageConverter());
        list.add(new SourceHttpMessageConverter<Source>());
        list.add(new AllEncompassingFormHttpMessageConverter());

        if (romePresent) {
            list.add(new AtomFeedHttpMessageConverter());
            list.add(new RssChannelHttpMessageConverter());
        }

        if (jackson2XmlPresent) {
            list.add(new MappingJackson2XmlHttpMessageConverter());
        } else if (jaxb2Present) {
            list.add(new Jaxb2RootElementHttpMessageConverter());
        }
        return list;
    }

    protected ConversionService defaultConversionService() {
        DefaultConversionService defaultConversionService = new DefaultConversionService();
        defaultConversionService.addConverter(new ServiceDateConverter());
        return defaultConversionService;
    }

    public ConversionService getConversionService() {
        return conversionService;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public List<HttpMessageConverter<?>> getHttpMessageConverters() {
        return httpMessageConverters;
    }

    public void setHttpMessageConverters(List<HttpMessageConverter<?>> httpMessageConverters) {
        this.httpMessageConverters = httpMessageConverters;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public List<Class> getIgnoredParamClasses() {
        return ignoredParamClasses;
    }

    public void setIgnoredParamClasses(List<Class> ignoredParamClasses) {
        this.ignoredParamClasses = ignoredParamClasses;
    }

    public void checkServiceConfiguration() {
        if (!StringUtils.hasText(this.server)) {
            throw new IllegalArgumentException("server is null");
        }
        if (null == this.conversionService) {
            throw new IllegalArgumentException("conversionService is null");
        }
        if (CollectionUtils.isEmpty(this.httpMessageConverters)) {
            throw new IllegalArgumentException("httpMessageConverters is null");
        }

    }
}
