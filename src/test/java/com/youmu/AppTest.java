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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Test
    public void testHashMap() {
        Map<MyOb, String> map = new HashMap<>();
        for (int i=0;i<11;i++){
            map.put(new MyOb(String.valueOf(1+i*16)),String.valueOf(i+1));
        }
        System.out.println();
    }

    static class MyOb {
        private String s;

        public MyOb(String s) {
            this.s = s;
        }

        @Override
        public int hashCode() {
            return Integer.valueOf(s);
        }
    }

    public static void add(Set<?> set) {
        set.remove(1);
    }

}
