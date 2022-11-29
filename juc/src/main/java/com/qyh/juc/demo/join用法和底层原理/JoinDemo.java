package com.qyh.juc.demo.join用法和底层原理;


//import lombok.extern.slf4j.Slf4j;

public class JoinDemo {
    public static void main(String[] args) throws InterruptedException {
        //1,不使用join
        //开启两个线程
        // Worker worker1 = new Worker();
        // Worker worker2 = new Worker();
        // worker1.start();
        // worker2.start();
        //打印结果
        // 11:28:12.022 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 1
        // 11:28:12.022 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 1
        // 11:28:12.027 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 2
        // 11:28:12.027 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 2
        // 11:28:12.027 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 3
        // 11:28:12.027 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 3
        // 11:28:12.027 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 4
        // 11:28:12.028 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 5
        // 11:28:12.028 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 4
        // 11:28:12.028 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 5
        //结果是交错打印
        //怎么顺序打印？
        // 2,使用join
        Worker worker1 = new Worker();
        Worker worker2 = new Worker();
        worker1.start();
        worker1.join();
        worker2.start();
        //打印结果
        ////11:29:43.932 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 1
        // 11:29:43.937 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 2
        // 11:29:43.937 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 3
        // 11:29:43.937 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 4
        // 11:29:43.937 [Thread-0] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 5
        // 11:29:43.937 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 1
        // 11:29:43.937 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 2
        // 11:29:43.937 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 3
        // 11:29:43.937 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 4
        // 11:29:43.937 [Thread-1] DEBUG com.qyh.juc.demo.join用法和底层原理.Worker - 5
        //结果 调用t.join()方法的线程会等待 t线程执行完毕之后才会执行
    }

}


//@Slf4j
class Worker extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
//            log.debug("{}", i + 1);
        }
    }
}
