package com.qyh.juc.demo.模拟实现原子类;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 获得unsafe类
 * @date 2022/11/29 20:16
 */

public class UnsafeUtils {

    public static Unsafe getUnsafe() throws IllegalAccessException, NoSuchFieldException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        return (Unsafe) theUnsafe.get(null);
    }
}
