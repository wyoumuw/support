package com.youmu;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Bytes;
import com.youmu.cache.RedisConfig;
import com.youmu.common.wrapper.Wrapper;
import org.junit.Test;
import org.springframework.test.annotation.SystemProfileValueSource;
import sun.misc.GC;

import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @Author: YOUMU
 * @Description: this all test methods need run in main method
 * @Date: 2017/11/13
 */
public class GcTest {

    public static final class A {
        {
            System.out.println("<init()>");
        }

        A() {
            System.out.println("A()");
        }

        public Comparable getComp() {
            return new C();
        }

        private class C implements Comparable {
            @Override
            public int compareTo(Object o) {
                return 0;
            }
        }
    }

    static Map m = new HashMap();

    @Test
    public void circleRef() {
        Scanner scanner = new Scanner(System.in);
        A a = new A();
        System.out.println("new A");
        scanner.next();
        Comparable c = a.getComp();
        System.out.println("getComp");
        scanner.next();
        a = null;
        c = null;// memory leak
        System.out.println("null");
        scanner.next();
    }

    public static final class Container {
        A a;

        public void newA() {
            a = new A();
        }
    }

    @Test
    public void innerClassIsConsumables() {
        Map map = Maps.newHashMap();
        List list = Lists.newArrayList();
        Iterator iterator = list.iterator();
    }

    public static void main2(String[] args) {
        Scanner scanner = new Scanner(System.in);
        A a = new A();
        System.out.println("new A");
        scanner.next();
        Comparable c = a.getComp();
        System.out.println("getComp");
        scanner.next();
        a = null;
        // c=null;//memory leak
        System.out.println("null");
        scanner.next();
        // Container c=new Container();
        // c.newA();
    }

    static WeakHashMap<String, byte[]> weakHashMap = new WeakHashMap<>();

    /**
     * test on -Xmx:2MB
     */
    @Test
    public void weakReferenceTest() {
        System.out.println(Runtime.getRuntime().freeMemory() / 1024.0 / 1024);
        // 给Map做一层代理
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            System.out.println(">>>>>>>>>>>>>>>invoke:" + method.getName());
            System.out.println(">>>>>>>>>>>>>>>map Size:" + weakHashMap.size());
            return method.invoke(weakHashMap, args);
        };
        Map map = (Map) Proxy.newProxyInstance(GcTest.class.getClassLoader(),
                new Class[] { Map.class }, invocationHandler);
        map.put("1", new byte[1024 * 1024]);
        map.put("2", new byte[1024 * 1024]);
        map.put("3", new byte[1024 * 1024]);
        map.put("4", new byte[1024 * 1024]);
        map.put("5", new byte[1024 * 1024]);
        map.put("6", new byte[1024 * 1024]);
        System.out.println(">>>>>>>>>>>>>>>map:" + weakHashMap);
    }

    public static void main(String[] as) {
        Scanner scanner = new Scanner(System.in);
        RedisConfig a = new RedisConfig();
        System.gc();
        scanner.next();
        Wrapper wrapper = new Wrapper(a);
        System.gc();
        scanner.next();
        wrapper = null;
        System.gc();
        scanner.next();
        System.out.println("over");
    }

    @Test
    public void test() {
        Object a = new Object();
        System.gc();
        Wrapper wrapper = new Wrapper(a);
        System.gc();
        wrapper = null;
        System.gc();
        System.out.println("over");
    }
}
