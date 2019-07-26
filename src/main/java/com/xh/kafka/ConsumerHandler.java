package com.xh.kafka;

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
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();

        Properties props = new Properties();
        // 获取元数据
        props.put("bootstrap.servers", KafkaConstant.BOOTSTRAP_SERVERS);
        // 指定消费者组
        props.put("group.id", "test-group4");
        // 手动提交位移
        props.put("enable.auto.commit", "false");
        // 添加该配置表示，如果kafka内部位移主题 __consumer_offsets 有存储消费位移，就从上次消费处开始消费
        // 如果没有 那么久重头开始消费
        props.put("auto.offset.reset", "earliest");
        // 序列化方式
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(KafkaConstant.TOPIC1));

        int count = 0;
        while (true) {
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                // 处理消息
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
                log.info("count : [{}]", count);
            }
        }
    }

}
