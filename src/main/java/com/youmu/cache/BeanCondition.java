package com.youmu.cache;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class BeanCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String[] s = context.getBeanFactory().getBeanNamesForType(CacheAnnotationHandler.class);
        return null == s || s.length == 0;
    }
}
