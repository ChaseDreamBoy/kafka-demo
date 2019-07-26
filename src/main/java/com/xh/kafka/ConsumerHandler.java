package com.xh.kafka;

import com.xh.kafka.util.ConsumerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author xiaohe
 * @version V1.0.0
 */
@Slf4j
public class ConsumerHandler {

    public static void main(String[] args) {
        // 单个
        Properties props = ConsumerUtils.generalConsumerProperties1(KafkaConstant.BOOTSTRAP_SERVERS, KafkaConstant.SINGLE_GROUP_ID);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(KafkaConstant.TOPIC1));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            ConsumerHandler consumerHandler = new ConsumerHandler();
            consumerHandler.fineGrainedCommitOffsetAsyncConsumer(consumer, records, "single thread");
        }
    }

    /**
     * 异步细粒度提交位移 方式 处理消息
     *
     * @param consumer   consumer
     * @param records    records
     * @param threadName thread name
     */
    void fineGrainedCommitOffsetAsyncConsumer(KafkaConsumer<String, String> consumer, ConsumerRecords<String, String> records, String threadName) {
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        int count = 0;
        for (ConsumerRecord<String, String> record : records) {
            // 处理消息
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("cur time : [{}], thread name : [{}]", System.currentTimeMillis(), threadName);
            log.info("handle message : topic is : [{}], partition is : [{}], offset is : [{}], key is : [{}], timestamp is [{}], value is : [{}]",
                    record.topic(), record.partition(), record.offset(), record.key(), record.timestamp(), record.value());
            // x出来完成的消息存储到集合中  后面统一提交
            offsets.put(new TopicPartition(record.topic(), record.partition()),
                    new OffsetAndMetadata(record.offset() + 1));
            if (count % 100 == 0) {
                // 异步细粒度提交位移
                consumer.commitAsync(offsets, (x, y) -> {
                    // 提交位移回调处理
                    // Map<TopicPartition, OffsetAndMetadata> var1  <==> x
                    for (TopicPartition topicPartition : x.keySet()) {
                        log.info("call back topicPartition, topic : [{}], partition : [{}]", topicPartition.topic(), topicPartition.partition());
                    }
                    // 异常处理
                    // Exception var2 <==> y
                    if (y != null) {
                        log.info("call back has error : [{}]", y.getMessage());
                    }
                });
            }
            count++;
            // log.info("count : [{}]", count);
        }
    }

}
