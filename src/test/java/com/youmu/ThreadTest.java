package com.youmu;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/17
 */
public class ThreadTest {

    static Object obj = new Object();

//    public static void main(String[] args) throws InterruptedException {
//        for (int i = 0; i < 5; i++) {
//            add();
//        }
//    }

    static void add() {
        synchronized (obj) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            try {
                executorService.submit(() -> {
                    System.out.println("start");
                    remove();
                }).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    static void remove() {
        synchronized (obj) {
            System.out.print("in");
        }
    }


    @Test
    public void Test() throws Exception {
        C c = new C();
        int max = 100000000;
        String ta = c.a;
        long time = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
            if (i % 2 == 0) {
                ta = c.a;
            } else {
                ta = "c.a";
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println(time);
    }

    static class C {
        String a = "1";
    }

    final Object lock = new Object();

    @Test
    public void bbTest() throws Exception {
        TimeUnit.SECONDS.sleep(5);
        ClassLayout classLayout = ClassLayout.parseInstance(lock);
        System.out.println("start" + classLayout.toPrintable());
        synchronized (lock) {
            System.out.println("start" + classLayout.toPrintable());
            Thread t = new Thread(() -> {
                System.out.println();
                System.out.println("before other thread lock" + classLayout.toPrintable());

                synchronized (lock) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("in other thread lock" + classLayout.toPrintable());
                }
                System.out.println("out other thread lock" + classLayout.toPrintable());
            });
            t.start();
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println("in lock" + classLayout.toPrintable());
        }
        System.out.println("out lock" + classLayout.toPrintable());

        int i=0;
        while (true) {
            TimeUnit.MILLISECONDS.sleep(500);
//            if(i++%2==0){
//                System.out.println("slp");
//            }
            if(i++==10){
                break;
            }
        }
        System.out.println(classLayout.toPrintable());
    }

//    public static void main(String[] args) throws Exception{
//        TimeUnit.SECONDS.sleep(5);
//        ThreadTest threadTest=new ThreadTest();
//        ClassLayout classLayout = ClassLayout.parseInstance(threadTest.lock);
//        System.out.println("start" + classLayout.toPrintable());
//        synchronized (threadTest.lock) {
//            System.out.println("in lock" + classLayout.toPrintable());
//        }
//        System.out.println("out lock" + classLayout.toPrintable());
//    }
    public static void main(String[] args) throws Exception{
//        TimeUnit.SECONDS.sleep(5);
        ThreadTest threadTest=new ThreadTest();
        System.out.println(ClassLayout.parseInstance(new int[]{1,3,4}).toPrintable());
        ClassLayout classLayout = ClassLayout.parseInstance(threadTest.lock);
        System.out.println("start" + classLayout.toPrintable());
        synchronized (threadTest.lock) {
            Thread t = new Thread(() -> {
                System.out.println();
                System.out.println("before other thread lock" + classLayout.toPrintable());

                synchronized (threadTest.lock) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("in other thread lock" + classLayout.toPrintable());
                }
                System.out.println("out other thread lock" + classLayout.toPrintable());
            });
            t.start();
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println("in lock" + classLayout.toPrintable());
        }
        System.out.println("out lock" + classLayout.toPrintable());

        int i=0;
        while (true) {
            TimeUnit.MILLISECONDS.sleep(500);
//            if(i++%2==0){
//                System.out.println("slp");
//            }
            if(i++==10){
                break;
            }
        }
        System.out.println(classLayout.toPrintable());
        TimeUnit.MILLISECONDS.sleep(5000);
        synchronized (threadTest.lock) {
            System.out.println(classLayout.toPrintable());
        }
    }

    @Test
    public void scheduleTest() throws  Exception{
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(()->{
            System.out.println("当前时间:"+ DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }, 0,1 , TimeUnit.SECONDS);
        new CyclicBarrier(2).await();

    }
}
