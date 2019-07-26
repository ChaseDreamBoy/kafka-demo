package com.xh.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @author xiaohe
 * @version V1.0.0
 */
public class ProducerHandler {

    public static void main(String[] args) {
        testProducer();
    }

    private static void testProducer() {
        Properties props = new Properties();
        // A list of host/port pairs to use for establishing the initial connection to the Kafka cluster.
        props.put("bootstrap.servers", KafkaConstant.BOOTSTRAP_SERVERS);
        // acks = [all，-1，0，1]
        // acks = all This means the leader will wait for the full set of in-sync replicas to acknowledge the record.
        // This guarantees that the record will not be lost as long as at least one in-sync replica remains alive.
        // This is the strongest available guarantee. This is equivalent to the acks=-1 setting.
        props.put("acks", "all");
        // re try  default 0   value = [0，...，2147483647]
        props.put("retries", 0);
        // default 16384    Buffer size
        props.put("batch.size", 16384);
        // default 0   The upper limit of delay
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 10010; i++) {
            producer.send(new ProducerRecord<>(KafkaConstant.TOPIC1, "key" + i, "test log 4 : " + i));
        }
        producer.close();
    }

}
