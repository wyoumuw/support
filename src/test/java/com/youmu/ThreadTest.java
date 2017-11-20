package com.youmu;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
}
