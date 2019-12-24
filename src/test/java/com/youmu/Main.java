package com.youmu;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.function.Function;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/16
 */
public class Main {
	public static void main(String[] args) {
		Set<Integer> set1= Sets.newHashSet(1,2,3,5);
		Set<Integer> set2= Sets.newHashSet(1,2,4,5);
		System.out.println(Sets.union(set1,null));
	}
	private static class A implements Runnable{
		@Override
		public void run() {

		}
	}
}