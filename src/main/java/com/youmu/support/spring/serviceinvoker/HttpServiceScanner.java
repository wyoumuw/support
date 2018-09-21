package com.youmu.support.spring.serviceinvoker;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.util.StringUtils;

import com.google.common.collect.Sets;

/**
 * @Author: YLBG-LDH-1506
 * @Description: service扫描器
 * @Date: 2018/08/15
 */
public class HttpServiceScanner extends ClassPathBeanDefinitionScanner {

    private static final String DEFAULT_SERVICE_INVOKER_FACTORY_NAME = DefaultServiceInvokerFactory.class
            .getCanonicalName();
    // fix more than one HttpServiceScanner to instance that using one
    // serviceInvokerFactory issue
    private static AtomicInteger instanceNum = new AtomicInteger(0);

    private String serviceConfigurationName;

    private String serviceInvokerFactoryName;

    public HttpServiceScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        instanceNum.incrementAndGet();
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        Set<BeanDefinitionHolder> serviceInvokerFactoryBeanDefinitionSet = Sets.newHashSet();
        // 如果不指定serviceInvokerFactory使用默认的
        if (!StringUtils.hasText(serviceInvokerFactoryName)) {
            GenericBeanDefinition serviceInvokerFactoryBeanDefine = new GenericBeanDefinition();
            serviceInvokerFactoryBeanDefine.setBeanClass(DefaultServiceInvokerFactory.class);
            serviceInvokerFactoryBeanDefine.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            serviceInvokerFactoryBeanDefine.getPropertyValues().add("serviceConfiguration",
                    new RuntimeBeanReference(serviceConfigurationName));
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(
                    serviceInvokerFactoryBeanDefine, getDefaultServiceInvokerFactoryName());
            serviceInvokerFactoryName = getDefaultServiceInvokerFactoryName();
            registerBeanDefinition(beanDefinitionHolder, getRegistry());
        }
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionHolder
                    .getBeanDefinition();
            beanDefinition.getConstructorArgumentValues()
                    .addGenericArgumentValue(beanDefinition.getBeanClassName());
            beanDefinition.setBeanClass(WebServiceFactoryBean.class);
            // predicate in ServiceProxy constructor
            beanDefinition.getPropertyValues().add("serviceConfiguration",
                    new RuntimeBeanReference(serviceConfigurationName));
            beanDefinition.getPropertyValues().add("serviceInvokerFactory",
                    new RuntimeBeanReference(serviceInvokerFactoryName));
        }
        return beanDefinitionHolders;
    }

    public String getServiceConfigurationName() {
        return serviceConfigurationName;
    }

    public void setServiceConfigurationName(String serviceConfigurationName) {
        this.serviceConfigurationName = serviceConfigurationName;
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

    private synchronized String getDefaultServiceInvokerFactoryName() {
        return DEFAULT_SERVICE_INVOKER_FACTORY_NAME + "#" + instanceNum.get();
    }
}