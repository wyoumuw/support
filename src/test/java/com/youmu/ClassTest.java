package com.youmu;

import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by wyoumuw on 2019/4/27.
 */
public class ClassTest {

    @Test
    public void test() throws MalformedURLException, ClassNotFoundException {
//        Class<?> caller = Reflection.getCallerClass();
        File file=new File("D:\\project\\support\\target\\test-classes");
        ClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()},null);
        ClassLoader c = new URLClassLoader(new URL[]{file.toURI().toURL()},null);
        Class p1 = Class.forName("com.youmu.Parent");
        Thread.currentThread().setContextClassLoader(c);
        Class p2 = Class.forName("com.youmu.Parent",true,c);
        System.out.println(p1.getClassLoader());
        System.out.println(p2.getClassLoader());
        System.out.println(p1 == p2);
    }

    @Test
    public void Test() throws  Exception{
        class A{
            long c;
            char i;
        }
        A a= new A();
        System.out.println(ClassLayout.parseInstance(a).toPrintable());;
    }
}

class YoumuClass {

}
