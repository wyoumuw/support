package com.youmu;

import com.youmu.utils.ReflectUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Test;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2018/09/13
 */
public class UtilsCompareTest {
    public static class A {
        String name;
        Integer i;
        Double l;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public Double getL() {
            return l;
        }

        public void setL(Double l) {
            this.l = l;
        }
    }

    public static class B {
        String name;
        Integer i;
        Double l;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public Double getL() {
            return l;
        }

        public void setL(Double l) {
            this.l = l;
        }
    }

    // 书写过于麻烦采用类生成的方式实现的 MapStruct
    @Test
    public void mapStructTest() {
        A a = new A();
        a.i = 10;
        a.l = 0.01;
        a.name = "youmu";
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
        }
    }

    @Test
    public void dozerTest() {
        A a = new A();
        a.i = 10;
        a.l = 0.01;
        a.name = "youmu";
        Mapper mapper = new DozerBeanMapper();
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            B b = mapper.map(a, B.class);
        }
        System.out.println(System.nanoTime() - startTime);
    }

    @Test
    public void reflectUtils() {
        A a = new A();
        a.i = 10;
        a.l = 0.01;
        a.name = "youmu";
        Mapper mapper = new DozerBeanMapper();
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            B b = ReflectUtils.copyProperties(a, B.class, null);
        }
        System.out.println(System.nanoTime() - startTime);
    }

}
