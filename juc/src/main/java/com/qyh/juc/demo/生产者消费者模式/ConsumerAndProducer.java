package com.qyh.juc.demo.生产者消费者模式;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

public class ConsumerAndProducer {
    public static void main(String[] args) throws InterruptedException {
        //创建一个大小为5的消息队列
        MessageQueue messageQueue = new MessageQueue(5);
        //模拟10个线程去放消息
        for (int i = 0; i < 10; i++) {
            int j = i;
            new Thread(() -> {
                messageQueue.add(new Message("这是第" + (j + 1) + "条信息"));
            }).start();
        }
        //模拟10个线程去取消息 但是有延迟

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            new Thread(() -> {
                messageQueue.get();
            }).start();
        }
    }
}


/**
 * @author K0n9D1KuA
 * @version 1.0
 * @description: 消息队列
 * @date 2022/11/29 12:05
 */


@Slf4j
class MessageQueue {
    //队列大小
    private final Integer queueSize;
    //消息队列
    private LinkedList<Message> messages;

    public MessageQueue(Integer queueSize) {
        this.queueSize = queueSize;
        this.messages = new LinkedList<Message>();
    }
    /*
     * @author: K0n9D1KuA
     * @description: 添加消息的方式
     * @param: message 消息
     * @return:
     * @date: 2022/11/29 12:10
     */

    public void add(Message message) {
        synchronized (messages) {
            while (messages.size() == queueSize) {
                //说明队列满了 需要等待
                log.debug("放不下消息了！  等待生产者拿去消息");
                try {
                    messages.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //添加消息
            messages.addLast(message);
            //添加消息成功
            log.debug("添加消息成功 消息内容:{}", message.getContent());
            //唤醒等待取消息的线程
            messages.notifyAll();
        }
    }

    /*
     * @author: K0n9D1KuA
     * @description: 获得消息的方法
     * @param: null
     * @return:
     * @date: 2022/11/29 12:14
     */

    public void get() {
        synchronized (messages) {
            while (messages.size() == 0) {
                //说明没有消息 需要等待
                try {
                    log.debug("没有消息了 先休息一会");
                    messages.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //说明有消息
            //获得消息
            Message message = messages.pollFirst();
            //消费消息
            log.debug("成功获得消息 {}", message.getContent());
            //唤醒等待放消息的线程
            messages.notifyAll();
        }
    }

}

//消息类
@Data
class Message {
    public Message(String content) {
        this.content = content;
    }

    //消息内容
    private String content;
}


