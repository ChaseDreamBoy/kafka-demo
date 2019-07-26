package com.xh.kafka;

import com.xh.kafka.util.ConsumerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 多线程消费 kafka 方案一：
 * <p>
 * 消费者程序启动多个线程，每个线程维护专属的 KafkaConsumer 实例，负责完整的消息获取、消息处理流程。
 * </p>
 *
 * @author xiaohe
 * @version V1.0.0
 */
@Slf4j
public class MultiThreadConsumerMethod1 implements Runnable {

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final KafkaConsumer<String, String> consumer;

    MultiThreadConsumerMethod1(String groupId) {
        Properties props = ConsumerUtils.generalConsumerProperties1(KafkaConstant.BOOTSTRAP_SERVERS, groupId);
        consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC1));
            while (!closed.get()) {
                // 执行消息处理逻辑
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                ConsumerHandler consumerHandler = new ConsumerHandler();
                consumerHandler.fineGrainedCommitOffsetAsyncConsumer(consumer, records, Thread.currentThread().getName());
            }
        } catch (WakeupException e) {
            // Ignore exception if closing
            if (!closed.get()) {
                throw e;
            }
        } finally {
            consumer.close();
        }
    }


    /**
     * Shutdown hook which can be called from a separate thread
     */
    public void shutdown() {
        closed.set(true);
        consumer.wakeup();
    }


}
