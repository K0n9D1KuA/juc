package com.qyh.juc.demo.join用法和底层原理;

import java.util.Random;

/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 假设这是t对象 现在模拟join的底层原理
 * @date 2022/11/29 11:35
 */

public class t {

    public final void join() {

    }

    /*
     * @author: K0n9D1KuA
     * @description: 模拟实现join函数
     * @param: mills 等待超时时间 等过多少s就不等了
     * @return:
     * @date: 2022/11/29 11:45
     */
    //注意被同步修饰符
    //说明锁的是t对象本身
    public final synchronized void join(long mills) throws InterruptedException {
        //获得当前系统时间 作为基准
        long base = System.currentTimeMillis();
        //经过了多少时间 最开始肯定为0
        long passTime = 0;
        //合法性参数校验
        if (mills < 0) {
            //超时时间小于0
            //抛出参数不合法异常
            throw new IllegalArgumentException("timeout value is negative");
        }
        if (mills == 0) {
            //说明是无限等待 知道t对象释放线程
            //如果线程还存活
            while (isAlive()) {
                //如果线程还存活
                //wait(0) 就是无限等待
                wait(0);
            }
        } else {
            while (isAlive()) {
                //说明线程还活着 需要等待
                //获得还剩多少等待时间
                //等待时间 = 超时时间 - 经过的时间
                long waitTime = mills - passTime;
                if (waitTime <= 0) {
                    //说明不应该再继续等待了 直接return
                    break;
                }
                //等待 waitTime
                wait(waitTime);
                //更新passTime
                passTime = System.currentTimeMillis() - base;
            }


        }

    }
    //===========================================================================
    //join()方法的底层是利用wait()方法实现。
    //join()方法是一个同步方法，当主线程调用t1.join()方法时，主线程先获得了t1对象的锁。
    //join()方法中调用了t1对象的wait()方法，使主线程进入了t1对象的等待池。
    //===========================================================================
    //join()方法源码疑问?
    //join源码中，只看到join()方法调用了wait()方法，
    //但是没看到有调用notify() 或者 notifyAll() 系列的唤醒方法，
    //那它是怎么唤醒的呢？如果不唤醒，那不就一直等待下去了吗？

    //原来啊，在java中，Thread类线程执行完run()方法后，
    // 一定会自动执行notifyAll()方法。
    // 因为线程在die的时候会释放持用的资源和锁，
    // 自动调用自身的notifyAll方法。
    //===========================================================================
    //join()方法的死锁
    //Thread.currentThread().join();
    //调用上述方法 当前线程就会阻塞 得不到唤醒
    //那么久造成了死锁
    //===========================================================================

    /*
     * @author: K0n9D1KuA
     * @description: 判断当前线程是否存活的方法
     * @param: null
     * @return:
     * @date: 2022/11/29 11:48
     */

    public static boolean isAlive() {
        //模拟一下
        Random random = new Random();
        return random.nextInt() % 2 == 0;
    }
}
