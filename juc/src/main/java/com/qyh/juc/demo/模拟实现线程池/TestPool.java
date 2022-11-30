package com.qyh.juc.demo.模拟实现线程池;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TestPool {

    public static void main(String[] args) {
        //1,死等
        ThreadPoll threadPoll = new ThreadPoll(2, 1000, TimeUnit.SECONDS, 2,
                ((runnableBlockingQueue, task) -> {
                    //各种不同的拒绝策略:
                    //1,死等
                    runnableBlockingQueue.put(task);
                    //2,带超时时间的等待
                    //runnableBlockingQueue.offer(task, 500, TimeUnit.MILLISECONDS);
                    //3,放弃执行任务
                    //log.debug("放弃执行任务..........");
                    //4,抛出异常
                    //throw new RuntimeException("任务执行失败，任务队列已满" + task);
                    //5,让调用者自己执行 这里的调用者就是main线程
                    //task.run();
                }));
        for (int i = 0; i < 8; i++) {
            int j = i;
            threadPoll.execute(() -> {
                log.debug("这是第{}个任务", j);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}


//任务队列
@Slf4j
class BlockingQueue<T> {
    //任务队列
    private final Deque<T> queue = new ArrayDeque<>();
    //锁
    private final ReentrantLock lock = new ReentrantLock();
    //生产者条件变量
    private final Condition fullWaitSet = lock.newCondition();
    //消费者条件变量
    private final Condition emptyWaitSet = lock.newCondition();
    //容量
    private final Integer capacity;

    public BlockingQueue(Integer capacity) {
        this.capacity = capacity;
    }

    //带超时时间的阻塞获取方法
    public T poll(long timeout, TimeUnit timeUnit) {
        //加锁
        lock.lock();
        try {
            //把其他的时间单位统一转化为纳秒
            long toNanos = timeUnit.toNanos(timeout);
            while (queue.isEmpty()) {
                //如果剩余超时时间已经<=0了 那么久无需等待
                if (toNanos <= 0) return null;
                //等待
                log.debug("队列中没有元素 开始等待........");
                try {
                    //awaitNanos的返回值就是 等待时间-已经经过的时间 也就是剩余时间
                    toNanos = emptyWaitSet.awaitNanos(toNanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //说明有元素可以取
            T t = queue.removeFirst();
            //唤醒等待放任务的线程
            fullWaitSet.signal();
            return t;
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    //阻塞获取
    public T take() {
        //加锁
        lock.lock();
        try {
            while (queue.isEmpty()) {
                //等待
                log.debug("队列中没有元素 开始等待........");
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //说明有元素可以取
            T t = queue.removeFirst();
            //唤醒等待放任务的线程
            fullWaitSet.signal();
            return t;
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    //带超时时间的添加方法
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        //时间转化为nacos
        long toNanos = timeUnit.toNanos(timeout);
        //上锁
        lock.lock();
        try {
            while (capacity == queue.size()) {
                if (toNanos <= 0) {
                    return false;
                }
                //说明已经满了 需要等待
                log.debug("任务队列已满，需要等待.......");
                try {
                    toNanos = fullWaitSet.awaitNanos(toNanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //说明可以放任务
            queue.addLast(task);
            //加入任务队列
            log.debug("加入任务队列....... 任务对象{}", task);
            //唤醒等待取任务的线程
            emptyWaitSet.signal();
            return true;
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    //阻塞添加
    public void put(T task) {
        //上锁
        lock.lock();
        try {
            while (capacity == queue.size()) {
                //说明已经满了 需要等待
                log.debug("任务队列已满，需要等待.......");
                try {
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //说明可以放任务
            queue.addLast(task);
            //加入任务队列
            log.debug("加入任务队列....... 任务对象{}", task);
            //唤醒等待取任务的线程
            emptyWaitSet.signal();
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    //获取大小
    public Integer getSize() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }


    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        //加锁 保证线程安全
        lock.lock();
        try {
            //判断任务队列是否已经满了
            if (queue.size() == capacity) {
                //满了
                //将权利下放到调用者决定
                rejectPolicy.reject(this, task);
            } else { //有空闲 将任务添加到任务队列
                log.debug("加入任务队列 , {}", task);
                //添加任务
                queue.addLast(task);
                //唤醒消费者线程
                emptyWaitSet.signal();
            }
        } finally {
            //释放锁
            lock.unlock();
        }
    }
}

//拒绝策略
@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}

@Slf4j
//线程池
class ThreadPoll {
    //任务队列
    private BlockingQueue<Runnable> taskQueue;
    //线程集合
    private HashSet<Worker> workers = new HashSet<>();
    //核心线程数
    private Integer coreSize;
    //获取任务的超时时间
    private long timeOut;
    //时间单位
    private TimeUnit timeUnit;
    //决绝策略
    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPoll(Integer coreSize, long timeOut, TimeUnit timeUnit, Integer capacity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeOut = timeOut;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(capacity);
        this.rejectPolicy = rejectPolicy;
    }

    //执行任务的方法
    public void execute(Runnable task) {
        //当任务数量小于核心数的时候 可以直接执行
        //如果任务数超过了核心数 那么加入任务队列
        synchronized (workers) {
            if (workers.size() < coreSize) {

                Worker worker = new Worker(task);
                log.debug("需要新增线程..... 线程对象:{} , 任务对象 {} ", worker, task);
                workers.add(worker);
                //开启任务
                worker.start();
            } else {
                //将调用决策下发给 调用者
                taskQueue.tryPut(rejectPolicy, task);
                //死等
                //带超时等待
                //放弃任务执行
                //抛出异常
                //让调用者自己执行任务
            }
        }
    }

    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            //执行任务
            //当task不为空的时候 执行任务
            //当task为空 说明任务执行完毕  再接着从任务队列中获取任
            //      task本身不为空 || 从任务队列中取到
            while (task != null || (task = taskQueue.take()) != null) {
                try {
                    log.debug("开始执行任务.......  {}", task);
                    task.run();
                } catch (Exception e) {
                    //出现异常 捕捉一下
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            synchronized (workers) {
                //说明没有任务可以获得了 那么就从任务队列中移除
                log.debug("线程对象被移除 {}", this);
                workers.remove(this);
            }
        }
    }
}
