package com.youmu;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2017/11/17
 */
public class ThreadTest {

	static Object obj=new Object();
	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			add();
		}
	}

	static void add(){
		synchronized (obj){
			ExecutorService executorService= Executors.newSingleThreadExecutor();
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
	static void remove(){
		synchronized (obj){
			System.out.print("in");
		}
	}
    static LongAdder longAdder=new LongAdder();
	@Test
	public void testAdder() throws InterruptedException {
		ExecutorService executorService=Executors.newFixedThreadPool(20);
		for (int i = 0; i < 40; i++) {
			executorService.submit(()->{
				longAdder.increment();
				long l=longAdder.sum();//you can see same value be print when you set a debug point on inner of sum() method
				System.out.println(l);
			});
		}
		executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);
	}

	AtomicLong atomicLong=new AtomicLong(0);
	@Test
	public void testAtomic() throws InterruptedException {
		ExecutorService executorService=Executors.newFixedThreadPool(20);
		for (int i = 0; i < 40; i++) {
			executorService.submit(()->{
				System.out.println(atomicLong.incrementAndGet());
			});
		}
		executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);
	}
}
