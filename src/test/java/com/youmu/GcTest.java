package com.youmu;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.test.annotation.SystemProfileValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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

		A(){
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
	static Map m=new HashMap();
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
		c=null;//memory leak
        System.out.println("null");
        scanner.next();
    }

	public static final class Container{
		A a;
		public void newA(){
			a=new A();
		}
	}

    @Test
    public void innerClassIsConsumables(){
    	Map map= Maps.newHashMap();
		List list=Lists.newArrayList();
		Iterator iterator=list.iterator();
	}
    public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		A a = new A();
		System.out.println("new A");
		scanner.next();
		Comparable c = a.getComp();
		System.out.println("getComp");
		scanner.next();
		a = null;
//		c=null;//memory leak
		System.out.println("null");
		scanner.next();
//    	Container c=new Container();
//    	c.newA();
    }
}
