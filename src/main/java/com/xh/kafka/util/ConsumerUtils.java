package com.xh.kafka.util;

import java.util.Properties;

/**
 * @author xiaohe
 * @version V1.0.0
 */
public class ConsumerUtils {

    public static Properties generalConsumerProperties1(String bootstrapServer, String groupId){
        Properties props = new Properties();
        // 获取元数据
        props.put("bootstrap.servers", bootstrapServer);
        // 指定消费者组
        props.put("group.id", groupId);
        // 手动提交位移
        props.put("enable.auto.commit", "false");
        // 添加该配置表示，如果kafka内部位移主题 __consumer_offsets 有存储消费位移，就从上次消费处开始消费
        // 如果没有 那么久重头开始消费
        props.put("auto.offset.reset", "earliest");
        // 序列化方式
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

}
