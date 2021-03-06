package com.youmu;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.youmu.cache.redis.RedisConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.youmu.cache.ExpireableConfig;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestConfig.class, ExpireableConfig.class, RedisConfig.class })
public class AppTest implements BeanFactoryAware {

    BeanFactory beanFactory;

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
