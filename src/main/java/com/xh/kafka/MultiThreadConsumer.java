package com.xh.kafka;

/**
 * @author xiaohe
 * @version V1.0.0
 */
public class MultiThreadConsumer {

    public static void main(String[] args) {
        // MultiThreadConsumerMethod1
//        MultiThreadConsumerMethod1 thread1 = new MultiThreadConsumerMethod1(KafkaConstant.MULTI_THREAD_GROUP_ID);
//        MultiThreadConsumerMethod1 thread2 = new MultiThreadConsumerMethod1(KafkaConstant.MULTI_THREAD_GROUP_ID);
//        MultiThreadConsumerMethod1 thread3 = new MultiThreadConsumerMethod1(KafkaConstant.MULTI_THREAD_GROUP_ID);
//
//        new Thread(thread1,"thread1").start();
//        new Thread(thread2,"thread2").start();
//        new Thread(thread3,"thread3").start();
        // MultiThreadConsumerMethod2
        MultiThreadConsumerMethod2 threadConsumerMethod2 = new MultiThreadConsumerMethod2(8);
        threadConsumerMethod2.multiThreadHandlerRecord();
    }

}
