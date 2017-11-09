package com.youmu;

import com.youmu.cache.CacheAnnotationHandler;
import com.youmu.cache.ExpireableConfig;
import com.youmu.cache.RedisConfig;
import com.youmu.cache.annotation.Expireable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestConfig.class, ExpireableConfig.class, RedisConfig.class })
public class AppTest implements BeanFactoryAware {

    BeanFactory beanFactory;

    @Test
    public void t1() {
        System.out.println(beanFactory.getBean(CacheAnnotationHandler.class));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
