package com.qyh.juc.demo.park_unpark;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j
public class ParkAndUnPark {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("运行..........");
            log.info("..............................");
            log.info("暂停..........");
            LockSupport.park();
            LockSupport.park();
            log.info("恢复运行..........");
            log.info("恢复运行..........");
            log.info("恢复运行..........");

        });
        thread.start();

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        LockSupport.unpark(thread);

    }
    //情况1，thread先park 然后 主线程 unpark 恢复运行
    //14:37:03.428 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 运行..........
    //14:37:03.431 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - ..............................
    //14:37:03.431 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 暂停..........
    //14:37:05.432 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 恢复运行..........
    //14:37:05.432 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 恢复运行..........
    //14:37:05.432 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 恢复运行..........
    //情况2，thread先 park 并且park两次   然后 主线程 unpark 一直阻塞着 因为park了两次 但是只unpark了一次
    //14:43:35.029 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 运行..........
    //14:43:35.031 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - ..............................
    //14:43:35.031 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 暂停..........
    //情况3，主线程先 unpark 然后 thread park  直接运行 不会停下来
    //14:47:42.686 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 运行..........
    //14:47:42.688 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - ..............................
    //14:47:42.688 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 暂停..........
    //14:47:42.688 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 恢复运行..........
    //14:47:42.688 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 恢复运行..........
    //14:47:42.688 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 恢复运行..........
    //情况4，主线程先 unpark 然后 thread park两次 还是会阻塞住 因为 unpark只抵消了一个park
    //14:49:49.331 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 运行..........
    //14:49:49.333 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - ..............................
    //14:49:49.333 [Thread-0] INFO com.qyh.jucprojects.park_unpark.ParkAndUnPark - 暂停..........


}
