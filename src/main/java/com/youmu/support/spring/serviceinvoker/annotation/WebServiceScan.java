package com.youmu.support.spring.serviceinvoker.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.youmu.support.spring.serviceinvoker.SimpleBeanNameGenerator;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;


/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/09/14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(WebServiceScannerRegistrar.class)
public @interface WebServiceScan {

    /**
     * 服务配置的名字
     * @return
     */
    String serviceConfigurationName();

    /**
     * webservice所在的包
     * @return
     */
    String basePackage();

    /**
     * 注解作为WebService的注解
     * @return
     */
    Class<?>[] webServiceAnnotations() default FeignClient.class;

    /**
     * 名字生成器
     * @return
     */
    Class<? extends BeanNameGenerator> beanNameGeneratorClass() default SimpleBeanNameGenerator.class;

    /**
     * 自定义ServiceInvokerFactory
     * @return
     */
    String ServiceInvokerFactoryName() default "";
}
