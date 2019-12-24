package com.youmu.support.spring.serviceinvoker.annotation;

import com.youmu.support.spring.serviceinvoker.annotation.WebServiceScan;
import com.youmu.support.spring.serviceinvoker.core.ServiceScanner;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;


/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2018/09/14
 */
public class WebServiceScannerRegistrar
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {

        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(WebServiceScan.class.getName()));
        ServiceScanner scanner = new ServiceScanner(registry);
        for (Class webServiceAnnotation : annoAttrs.getClassArray("webServiceAnnotations")) {
            scanner.addIncludeFilter(new AnnotationTypeFilter(webServiceAnnotation));
        }
        Object o = BeanUtils.instantiate(annoAttrs.getClass("beanNameGeneratorClass"));
        scanner.setBeanNameGenerator((BeanNameGenerator) o);
        scanner.setServiceConfigurationName(annoAttrs.getString("serviceConfigurationName"));
        if (null != resourceLoader) {
            scanner.setResourceLoader(this.resourceLoader);
        }
        String ServiceInvokerFactoryName = annoAttrs.getString("ServiceInvokerFactoryName");
        if (StringUtils.hasText(ServiceInvokerFactoryName)) {
            scanner.setServiceInvokerFactoryName(ServiceInvokerFactoryName);
        }
        scanner.scan(StringUtils.tokenizeToStringArray(annoAttrs.getString("basePackage"),
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }
}
