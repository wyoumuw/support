package com.youmu.support.spring.serviceinvoker;

import java.util.List;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

/**
 * @Author: youmu
 * @Description:
 * @Date: 2018/08/15
 */
public class HttpServiceScannerConfigure
        implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ServiceConfiguration serviceConfiguration;
    private String basePackage;

    private List<Class> webServiceAnnotations = Lists.newArrayList(FeignClient.class);

    private ApplicationContext applicationContext;

    private String ServiceInvokerFactoryName;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        HttpServiceScanner scanner = new HttpServiceScanner(registry);
        for (Class webServiceAnnotation : webServiceAnnotations) {
            scanner.addIncludeFilter(new AnnotationTypeFilter(webServiceAnnotation));
        }
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

    public List<Class> getWebServiceAnnotations() {
        return webServiceAnnotations;
    }

    public void setWebServiceAnnotations(List<Class> webServiceAnnotations) {
        Assert.notEmpty(webServiceAnnotations, "webServiceAnnotations");
        this.webServiceAnnotations = webServiceAnnotations;
    }

    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }

    public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
        Assert.notNull(serviceConfiguration, "serviceConfiguration");
        this.serviceConfiguration = serviceConfiguration;
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

    static class HttpServiceScanner extends ClassPathBeanDefinitionScanner {

        private ServiceConfiguration serviceConfiguration;

        private String serviceInvokerFactoryName;

        private ServiceInvokerFactory serviceInvokerFactory;

        public HttpServiceScanner(BeanDefinitionRegistry registry) {
            super(registry, false);
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
            // 如果不指定serviceInvokerFactory使用默认的
            if (!StringUtils.hasText(serviceInvokerFactoryName) && null == serviceInvokerFactory) {
                serviceInvokerFactory = new DefaultServiceInvokerFactory();
                serviceInvokerFactory.setServiceConfiguration(serviceConfiguration);
            }
            for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionHolder
                        .getBeanDefinition();
                beanDefinition.getConstructorArgumentValues()
                        .addGenericArgumentValue(beanDefinition.getBeanClassName());
                beanDefinition.setBeanClass(WebServiceFactoryBean.class);
                // predicate in ServiceProxy constructor
                beanDefinition.getPropertyValues().add("serviceConfiguration",
                        serviceConfiguration);
                if (null != serviceInvokerFactory) {
                    beanDefinition.getPropertyValues().add("serviceInvokerFactory",
                            serviceInvokerFactory);
                } else {
                    beanDefinition.getPropertyValues().add("serviceInvokerFactory",
                            new RuntimeBeanReference(serviceInvokerFactoryName));
                }
            }
            return beanDefinitionHolders;
        }

        public ServiceConfiguration getServiceConfiguration() {
            return serviceConfiguration;
        }

        public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
            this.serviceConfiguration = serviceConfiguration;
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return beanDefinition.getMetadata().isInterface()
                    && beanDefinition.getMetadata().isIndependent();
        }

        public String getServiceInvokerFactoryName() {
            return serviceInvokerFactoryName;
        }

        public void setServiceInvokerFactoryName(String serviceInvokerFactoryName) {
            this.serviceInvokerFactoryName = serviceInvokerFactoryName;
        }
    }
}
