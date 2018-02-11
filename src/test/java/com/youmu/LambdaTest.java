package com.youmu;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.collect.Lists;
import org.springframework.scripting.groovy.GroovyScriptFactory;
import org.springframework.util.StringUtils;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2017/11/16
 */
public class LambdaTest {

    public static final String a = "123";
    public static final int a1 = 0;

    public static void main(String[] args) {
    }

    @Test
    public void test() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class c = lookup.lookupClass();
        MethodHandle mh = lookup.findStatic(c, "println",
                MethodType.methodType(void.class, String.class));
        mh.invoke("hey!");
    }

    public void t() {
        List<Base> list = Lists.newArrayList();
        List<String> l = Lists.newArrayList();
        l = list.stream().map(Base::getName).collect(Collectors.toList());
    }

    static class Base {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void lambdaInvokeTest() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class c = lookup.lookupClass();
        MethodHandle mh = lookup.findStatic(c, "println",
                MethodType.methodType(void.class, String.class));
        CallSite callSite = LambdaMetafactory.metafactory(lookup, "println",
                MethodType.methodType(void.class, String.class),
                MethodType.methodType(void.class, String.class), mh,
                MethodType.methodType(void.class, String.class));
        callSite.dynamicInvoker().invoke("haha");
    }

    public static void println(String str) {
        System.out.println("LambdaTest.println---" + str);
    }

    public static String get() {
        return "youmu";
    }

    @Test
    public void s() {
        List<Integer> integers = Lists.newArrayList();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            integers.add(random.nextInt(10));
        }
        System.out.println(StringUtils.collectionToDelimitedString(integers, ","));
    }
}
