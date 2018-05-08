package com.youmu.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.youmu.cache.ExpireableImportSelector;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

import com.youmu.cache.ExpireableConfig;
import org.springframework.core.Ordered;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(ExpireableImportSelector.class)
public @interface EnableExpireableCache {

	boolean proxyTargetClass() default false;

	AdviceMode mode() default AdviceMode.PROXY;

	int order() default Ordered.LOWEST_PRECEDENCE;
}
