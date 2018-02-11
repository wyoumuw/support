package com.youmu;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.junit.Test;

import org.springframework.util.ReflectionUtils;
import sun.misc.ProxyGenerator;

import javax.security.auth.login.Configuration;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2017/11/15
 */
public class ProxyTest {

    public static void main(String[] args) {
        byte[] klass = ProxyGenerator.generateProxyClass("com.youmu.$Proxy1",
                new Class[] { MyService.class }, Modifier.PUBLIC | Modifier.FINAL);
        try (FileOutputStream fileOutputStream = new FileOutputStream("E://$Proxy1.class")) {
            fileOutputStream.write(klass);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void proxyTest() {
        MyService myServiceImpl = new MyServiceImpl();
        InvocationHandler invocationHandler = new InvocationHandler() {
            Object target = myServiceImpl;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.printf("ProxyTest.invoke:%s%n", method.getName());
                Object rtn = method.invoke(target, args);
                return rtn;
            }
        };
        MyService myService = (MyService) Proxy.newProxyInstance(ProxyTest.class.getClassLoader(),
                new Class[] { MyService.class }, invocationHandler);
        myService.print1();
        myService.print2();
    }

    static class A {
        private Object obj;
    }

    @Test
    public void tst() throws IllegalAccessException {
        A a = new A();
        a.obj = new Object();
        Field objField = ReflectionUtils.findField(A.class, "obj");
        objField.setAccessible(true);
        System.out.println(objField.get(a));
        a.obj = new Object();
        System.out.println(objField.get(a));
    }

    @Test
    public void security() throws PrivilegedActionException {
        String finalClass = "sun.security.provider.ConfigFile";
        Configuration untrustedImpl = AccessController
                .doPrivileged(new PrivilegedExceptionAction<Configuration>() {
                    public Configuration run() throws ClassNotFoundException,
                            InstantiationException, IllegalAccessException {
                        Class<? extends Configuration> implClass = Class
                                .forName(finalClass, false,
                                        Thread.currentThread().getContextClassLoader())
                                .asSubclass(Configuration.class);
                        return implClass.newInstance();
                    }
                });
        System.out.println(untrustedImpl);
    }
}

class MyServiceImpl implements MyService {

    @Override
    public void print1() {
        System.out.println("MyServiceImpl.print1");
    }

    @Override
    public void print2() {
        System.out.println("MyServiceImpl.print2");
    }
}
