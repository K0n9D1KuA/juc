package com.qyh.juc.demo.模拟实现原子类;

import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.List;

public class MyAutomicIntegerDemo {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            testUnsafeAtomicInteger();
            testSafeAutomicInteger();
        }
    }
    /*
     * @author: K0n9D1KuA
     * @description: 测试不安全的原子类的线程安全问题
     * @param: null
     * @return:
     * @date: 2022/11/29 20:31
     */

    public static void testUnsafeAtomicInteger() {
        UnsafeAtomicInteger unsafeAtomicInteger = new UnsafeAtomicInteger(10000);
        List<Thread> ret = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                unsafeAtomicInteger.update(10);
            });
            thread.start();
            ret.add(thread);
        }
        //等待所有的线程
        for (Thread t : ret
        ) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //输出最后的结果
        System.out.println("不安全的原子类的最后的结果是:" + unsafeAtomicInteger.getValue());
    }

    /*
     * @author: K0n9D1KuA
     * @description: 测试安全的原子类线程安全问题
     * @param: null
     * @return:
     * @date: 2022/11/29 20:32
     */

    public static void testSafeAutomicInteger() {
        SafeAtomicInteger atomicInteger = new SafeAtomicInteger(10000);
        List<Thread> ret = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                atomicInteger.decrement(10);
            });
            thread.start();
            ret.add(thread);
        }
        //等待所有的线程
        for (Thread t : ret
        ) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //输出最后的结果
        System.out.println("安全的原子类最后的结果是:" + atomicInteger.getValue());
    }
    // 安全的原子类最后的结果是:0
    // 不安全的原子类的最后的结果是:0
    // 安全的原子类最后的结果是:0
    // 不安全的原子类的最后的结果是:0
    // 安全的原子类最后的结果是:0
    // 不安全的原子类的最后的结果是:0
    // 安全的原子类最后的结果是:0
    // 不安全的原子类的最后的结果是:0
    // 安全的原子类最后的结果是:0
    // 不安全的原子类的最后的结果是:0
    // 安全的原子类最后的结果是:0
    // 不安全的原子类的最后的结果是:0
    // 安全的原子类最后的结果是:0
    // 不安全的原子类的最后的结果是:0
    // 安全的原子类最后的结果是:0
    // 不安全的原子类的最后的结果是:0
    // 不安全的原子类的最后的结果是:10
    // 安全的原子类最后的结果是:0



}

class UnsafeAtomicInteger {
    private int value;

    public UnsafeAtomicInteger(int value) {
        this.value = value;
    }


    //获得值得方法
    public int getValue() {
        return value;
    }

    //修改
    public void update(int count) {
        this.value -= count;
    }
}

class SafeAtomicInteger {
    //修改的值
    private volatile int value;
    //Unsafe对象
    public static Unsafe UNSAFE;
    //value的偏移量
    public static long VALUE_OFFSET;

    //构造方法
    public SafeAtomicInteger(int value) {
        this.value = value;
    }

    static {
        try {
            //获得unsafe对象
            UNSAFE = UnsafeUtils.getUnsafe();
            VALUE_OFFSET = UNSAFE.objectFieldOffset(SafeAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //获得值得方法
    public int getValue() {
        return value;
    }

    //值减少的方法
    public void decrement(int amout) {
        while (true) {
            //获得之前的值
            int prev = this.value;
            //获得修改后的值
            int next = this.value - amout;
            //修改成功退出
            //参数 1,要修改的字段所属对象 2,修改变量的地址偏移量 3，之前的值 4，之后的值
            if (UNSAFE.compareAndSwapInt(this, VALUE_OFFSET, prev, next)) {
                break;
            }
        }

    }


}

