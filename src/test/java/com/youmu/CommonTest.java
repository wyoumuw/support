package com.youmu;

import com.google.common.collect.Lists;
import com.youmu.common.wrapper.IntWrapper;
import org.junit.Test;

import java.util.List;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2017/12/01
 */
public class CommonTest {
	int i=0;
    @Test
    public void t() {
        List<Object> list = Lists.newArrayList(1,2,3,4,5);
		List<Object> list2 = Lists.newArrayList(2,3,4,5);
		list.stream().peek(o->i++).forEach(System.out::println);
    }
}
