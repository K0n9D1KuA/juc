package com.qyh.juc.demo.Interrupt方法;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "InterruptDemo")
public class InterruptDemo {
    public static void main(String[] args) throws InterruptedException {
        interruptPark();
    }

    /**
     * 不正常的打断   **阻塞**(sleep wait join…)的线程
     *
     * @throws InterruptedException
     */
    public static void interrupt() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("sleep.........");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("被不正常的打断了");
            }
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        log.debug("打断");
        t1.interrupt();
        log.debug("打断标记  {} ", t1.isInterrupted());
        //打印结果：
        //14:20:41.559 [t1] DEBUG InterruptDemo - sleep.........
        //14:20:42.569 [main] DEBUG InterruptDemo - 打断
        //被不正常的打断了
        //14:20:42.569 [main] DEBUG InterruptDemo - 打断标记  true
        //java.lang.InterruptedException: sleep interrupted
    }

    /**
     * 正常的打断  此种情况下 线程不会停下来
     *
     * @throws InterruptedException
     */
    public static void normalInterrupt() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
//                log.debug("死循环.........");
            }
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        log.debug("打断");
        t1.interrupt();
        log.debug("打断标记  {} ", t1.isInterrupted());
        //最后的结果 被打断的线程不会停下来  会一直进行死循环
        //14:25:10.401 [main] DEBUG InterruptDemo - 打断
        //14:25:10.403 [main] DEBUG InterruptDemo - 打断标记  true

    }

    /**
     * 通过打断标记来优雅的停止线程 处于正常运行的线程 被其他线程打断 那么他的打断笔记就会被置为true
     * 通过打断标记来优雅的停止线程 处于正常运行的线程
     * 被其他线程打断的花 他的打断标记会被变为true
     */
    public static void stopElegantly() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                //获取当前线程
                boolean interrupted = Thread.currentThread().isInterrupted();
                if (interrupted) {
                    //如果被打断了 那么久退出循环 结束线程
                    log.debug("线程结束........");
                    return;
                } else {
                    log.debug("死循环");
                }
            }
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        log.debug("开始打断");
        t1.interrupt();
        log.debug("打断标记  {} ", t1.isInterrupted());
        //打印结果
        //14:27:58.585 [t1] DEBUG InterruptDemo - 死循环
        //14:27:58.585 [t1] DEBUG InterruptDemo - 死循环
        //14:27:58.585 [t1] DEBUG InterruptDemo - 死循环
        //14:27:58.585 [t1] DEBUG InterruptDemo - 死循环
        //14:27:58.585 [t1] DEBUG InterruptDemo - 死循环
        //14:27:58.585 [t1] DEBUG InterruptDemo - 死循环
        //14:27:58.585 [main] DEBUG InterruptDemo - 开始打断
        //14:27:58.585 [t1] DEBUG InterruptDemo - 线程结束........
        //14:27:58.585 [main] DEBUG InterruptDemo - 打断标记  true
        //进程已结束，退出代码为 0
    }

    /**
     * 打断park状态下的线程   当打断标记为true的情况下  park方法就会失效
     *
     * @throws InterruptedException
     */
    public static void interruptPark() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("park......");
            LockSupport.park();
            log.info("unPark.......");
            log.info("打断状态 : {}", Thread.currentThread().isInterrupted());
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        t1.interrupt();
        //14:29:57.136 [t1] INFO InterruptDemo - park......
        //14:29:58.140 [t1] INFO InterruptDemo - unPark.......
        //14:29:58.140 [t1] INFO InterruptDemo - 打断状态 : true
    }
}
