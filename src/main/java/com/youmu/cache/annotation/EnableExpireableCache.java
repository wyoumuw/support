package com.youmu.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.youmu.cache.ExpireableConfig;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(ExpireableConfig.class)
public @interface EnableExpireableCache {
}
