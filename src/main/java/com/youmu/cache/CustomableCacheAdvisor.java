package com.youmu.cache;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.StringUtils;

import com.youmu.cache.annotation.Expireable;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/08
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CustomableCacheAdvisor extends AbstractBeanFactoryPointcutAdvisor
        implements InitializingBean, PriorityOrdered {

    private Pointcut pointcut = AnnotationMatchingPointcut.forMethodAnnotation(Expireable.class);

    private CacheAnnotationHandler cacheAnnotationHandler;

    public CustomableCacheAdvisor() {
    }

    public CustomableCacheAdvisor(Advice interceptor) {
        setAdvice(interceptor);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == getAdvice() && StringUtils.isEmpty(getAdviceBeanName())) {
            if (null == cacheAnnotationHandler) {
                throw new NullPointerException(
                        "advise or prop[cacheAnnotationHandler] cannot be null!");
            }
            setAdvice(new CustomableCacheInterceptor(cacheAnnotationHandler));
        }
    }

    public void setCacheAnnotationHandler(CacheAnnotationHandler cacheAnnotationHandler) {
        this.cacheAnnotationHandler = cacheAnnotationHandler;
    }
}
