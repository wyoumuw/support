package com.youmu.support.spring.serviceinvoker.core;

import java.util.List;

import com.youmu.support.spring.serviceinvoker.SimpleBeanNameGenerator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

/**
 * @Author: YOUMU
 * @Description: service扫描器配置
 * @Date: 2018/08/15
 */
public class ServiceScannerConfigure
        implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private String serviceConfigurationName;
    private String basePackage;

    private List<Class> webServiceAnnotations = Lists.newArrayList(FeignClient.class);

    private ApplicationContext applicationContext;

    private BeanNameGenerator beanNameGenerator = new SimpleBeanNameGenerator();

    private String ServiceInvokerFactoryName;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        ServiceScanner scanner = new ServiceScanner(registry);
        for (Class webServiceAnnotation : webServiceAnnotations) {
            scanner.addIncludeFilter(new AnnotationTypeFilter(webServiceAnnotation));
        }
        scanner.setBeanNameGenerator(beanNameGenerator);
        scanner.setServiceConfigurationName(serviceConfigurationName);
        scanner.setResourceLoader(this.applicationContext);
        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        // do not things
    }

    public List<Class> getWebServiceAnnotations() {
        return webServiceAnnotations;
    }

    public void setWebServiceAnnotations(List<Class> webServiceAnnotations) {
        Assert.notEmpty(webServiceAnnotations, "webServiceAnnotations");
        this.webServiceAnnotations = webServiceAnnotations;
    }

    public String getServiceConfigurationName() {
        return serviceConfigurationName;
    }

    public void setServiceConfigurationName(String serviceConfigurationName) {
        Assert.hasText(serviceConfigurationName, "serviceConfiguration");
        this.serviceConfigurationName = serviceConfigurationName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        Assert.hasText(basePackage, "basePackage");
        this.basePackage = basePackage;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getServiceInvokerFactoryName() {
        return ServiceInvokerFactoryName;
    }

    public void setServiceInvokerFactoryName(String serviceInvokerFactoryName) {
        ServiceInvokerFactoryName = serviceInvokerFactoryName;
    }

    public BeanNameGenerator getBeanNameGenerator() {
        return beanNameGenerator;
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }
}
