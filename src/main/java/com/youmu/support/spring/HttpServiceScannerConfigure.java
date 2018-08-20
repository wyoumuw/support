package com.youmu.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/08/15
 */
public class HttpServiceScannerConfigure
        implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ServiceConfiguration serviceConfiguration;
    private String basePackage;

    private Class webServiceAnnotation = FeignClient.class;
    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        HttpServiceScanner scanner = new HttpServiceScanner(registry);
        scanner.addIncludeFilter(new AnnotationTypeFilter(webServiceAnnotation));
        scanner.setServiceConfiguration(serviceConfiguration);
        scanner.setResourceLoader(this.applicationContext);
        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        // do not things
    }

    public Class getWebServiceAnnotation() {
        return webServiceAnnotation;
    }

    public void setWebServiceAnnotation(Class webServiceAnnotation) {
        this.webServiceAnnotation = webServiceAnnotation;
    }

    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }

    public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    static class HttpServiceScanner extends ClassPathBeanDefinitionScanner {

        private ServiceConfiguration serviceConfiguration;

        public HttpServiceScanner(BeanDefinitionRegistry registry) {
            super(registry, false);
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
            for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionHolder
                        .getBeanDefinition();
                beanDefinition.getConstructorArgumentValues()
                        .addGenericArgumentValue(beanDefinition.getBeanClassName());
                beanDefinition.setBeanClass(WebServiceFactoryBean.class);
                // predicate in ServiceProxy constructor
                beanDefinition.getPropertyValues().add("serviceConfiguration",
                        serviceConfiguration);
            }
            return beanDefinitionHolders;
        }

        public ServiceConfiguration getServiceConfiguration() {
            return serviceConfiguration;
        }

        public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
            this.serviceConfiguration = serviceConfiguration;
        }
    }
}
