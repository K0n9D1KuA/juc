package com.qyh.juc.demo.ReentrantLock_多条件变量;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class MultipleConditionalVariable {
    //锁
    public static ReentrantLock ROOM = new ReentrantLock();
    //是否有烟
    public static Boolean hasCigarette = false;
    //是否有酒
    public static Boolean hasWine = false;
    //等烟的休息室
    public static Condition waitCigaretter = ROOM.newCondition();
    //等酒的休息室
    public static Condition waitWine = ROOM.newCondition();

    public static void main(String[] args) {
        new Thread(() -> {
            ROOM.lock();
            try {
                //上锁
                log.info("有烟么？ {}", hasCigarette);
                while (!hasCigarette) {
                    log.info("没有烟，开摆！");
                    try {
                        waitCigaretter.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //跳出循环 说明有烟了
                log.debug("开始干活");
            } finally {
                //释放锁
                ROOM.unlock();
            }
        }, "等烟的人").start();
        new Thread(() -> {
            ROOM.lock();
            try {
                //上锁
                log.info("有酒么？ {}", hasWine);
                while (!hasWine) {
                    log.info("没有酒，开摆！");
                    try {
                        waitWine.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //跳出循环 说明有烟了
                log.debug("开始干活");
            } finally {
                //释放锁
                ROOM.unlock();
            }
        }, "等酒的人").start();
        //等待2s 模拟送烟和酒
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            //上锁
            ROOM.lock();
            try {
                log.debug("酒到了");
                hasWine = true;
                waitWine.signalAll();
            } finally {
                ROOM.unlock();
            }
        }, "送酒的人").start();
        new Thread(() -> {
            //上锁
            ROOM.lock();
            try {
                log.debug("烟到了");
                hasCigarette = true;
                waitCigaretter.signalAll();
            } finally {
                ROOM.unlock();
            }
        }, "送烟的人").start();
    }
    //打印结果

    //15:03:21.035 [等烟的人] INFO com.qyh.jucprojects.ReentrantLock_多条件变量.MultipleConditionalVariable - 有烟么？ false
    //15:03:21.039 [等烟的人] INFO com.qyh.jucprojects.ReentrantLock_多条件变量.MultipleConditionalVariable - 没有烟，开摆！
    //15:03:21.039 [等酒的人] INFO com.qyh.jucprojects.ReentrantLock_多条件变量.MultipleConditionalVariable - 有酒么？ false
    //15:03:21.039 [等酒的人] INFO com.qyh.jucprojects.ReentrantLock_多条件变量.MultipleConditionalVariable - 没有酒，开摆！
    //15:03:23.048 [送酒的人] DEBUG com.qyh.jucprojects.ReentrantLock_多条件变量.MultipleConditionalVariable - 酒到了
    //15:03:23.048 [等酒的人] DEBUG com.qyh.jucprojects.ReentrantLock_多条件变量.MultipleConditionalVariable - 开始干活
    //15:03:23.048 [送烟的人] DEBUG com.qyh.jucprojects.ReentrantLock_多条件变量.MultipleConditionalVariable - 烟到了
    //15:03:23.048 [等烟的人] DEBUG com.qyh.jucprojects.ReentrantLock_多条件变量.MultipleConditionalVariable - 开始干活
}
