package com.youmu;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    public static final class Container{
		A a;
		public void newA(){
			a=new A();
		}
	}

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
        // c=null;//memory leak
        System.out.println("null");
        scanner.next();
    }

    @Test
    public void innerClassIsConsumables(){
		List<Integer> l = new ArrayList<>();
		l.add(1);
		l.add(2);
		l.add(3);
		l.add(4);
		l.add(5);
		l.add(6);
		for (Integer integer : l) {
			if (integer.intValue() == 4) {
				l.remove(integer);
			}
		}
	}

    public static void main(String[] args) {
    	Container c=new Container();
    	c.newA();
    }
}
