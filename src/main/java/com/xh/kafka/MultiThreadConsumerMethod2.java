package com.xh.kafka;

import com.xh.kafka.util.ConsumerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 这里使用单线程从kafka拉取消息.
 * 也可以参照 MultiThreadConsumerMethod1 使用多线程从kafka拉取消息.
 *
 * @author xiaohe
 * @version V1.0.0
 */
@Slf4j
public class MultiThreadConsumerMethod2 {

    private ExecutorService executors;

    private final KafkaConsumer<String, String> consumer;

    MultiThreadConsumerMethod2(int workerNum) {
        Properties props = ConsumerUtils.generalConsumerProperties1(KafkaConstant.BOOTSTRAP_SERVERS, KafkaConstant.SINGLE_GROUP_ID);
        consumer = new KafkaConsumer<>(props);
        executors = new ThreadPoolExecutor(
                workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
    }


    public void multiThreadHandlerRecord() {
        consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC1));
        while (true) {
            Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
            int count = 0;
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (final ConsumerRecord record : records) {
                executors.submit(new MyWork(record));
                offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
                if (count % 100 == 0) {
                    // 异步细粒度提交位移
                    // 回调处理 为 null
                    consumer.commitAsync(offsets, null);
                }
                count++;
                log.info("count ： [{}]", count);
            }
        }
    }

    @Slf4j
    private static class MyWork implements Runnable {

        ConsumerRecord record;

        MyWork(ConsumerRecord record) {
            this.record = record;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1);
                log.info("cur time : [{}]", System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("handle message : topic is : [{}], partition is : [{}], offset is : [{}], key is : [{}], timestamp is [{}], value is : [{}]",
                    record.topic(), record.partition(), record.offset(), record.key(), record.timestamp(), record.value());

        }
    }


}
