package com.youmu.support.spring.serviceinvoker;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @Author: YOUMU
 * @Description: 过滤所有被标记FeignClient的类，不让mvc处理他
 * @Date: 2018/10/31
 */
public class FilterFeignClientRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected boolean isHandler(Class<?> beanType) {
        return super.isHandler(beanType)
                && (AnnotationUtils.findAnnotation(beanType, FeignClient.class) == null);
    }
}
