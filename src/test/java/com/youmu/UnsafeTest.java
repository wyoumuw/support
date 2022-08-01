package com.youmu;

import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by wyoumuw on 2019/7/16.
 */
public class UnsafeTest {
    @Test
    public void t1() throws NoSuchFieldException, IllegalAccessException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(Unsafe.class);


        long address = unsafe.allocateMemory(8);
        unsafe.putLong(address, 0);
        long ll = 0;
        ll = ll >>> 1;
        ll ^= (1L << 52);
        unsafe.putLong(address, ll);
        System.out.println(unsafe.getDouble(address));
        unsafe.freeMemory(address);
    }

//
//    double a = 0;
//    uint64_t i = *(uint64_t *)&a;
//    i =(~0ULL)>>1;
//    i ^=(1ULL <<52);
//    double ans = *(double*)&i;
//    cout <<ans <<endl;
}
