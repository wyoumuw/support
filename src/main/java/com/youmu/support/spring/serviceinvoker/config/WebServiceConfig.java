package com.youmu.support.spring.serviceinvoker.config;

import com.youmu.support.spring.serviceinvoker.FilterFeignClientRequestMappingHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @Author: YOUMU
 * @Description: 防止写feign接口上写@RequestMapping时也会被springmvc认为是本服务对外接口
 * @Date: 2019/01/22
 */
@Configuration
public class WebServiceConfig {

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new FilterFeignClientRequestMappingHandlerMapping();
    }
}
