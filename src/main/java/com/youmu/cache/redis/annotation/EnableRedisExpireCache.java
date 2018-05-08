package com.youmu.cache.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.youmu.cache.redis.RedisConfig;
import org.springframework.context.annotation.Import;


/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(RedisConfig.class)
public @interface EnableRedisExpireCache {
}
