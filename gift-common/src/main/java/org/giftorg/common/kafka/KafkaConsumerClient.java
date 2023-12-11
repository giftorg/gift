/**
 * Copyright 2023 GiftOrg Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.giftorg.common.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.giftorg.common.config.Config;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaConsumerClient {

    public KafkaConsumer<String, String> kafkaConsumer;

    public KafkaConsumerClient(String topic, String groupId) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", Config.kafkaConfig.getHostUrl());
        props.setProperty("group.id", groupId);
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("max.poll.records", "1");

        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Arrays.asList(topic));
    }

    /**
     * 拉取消息
     */
    public ConsumerRecords<String, String> poll(int timeout) {
        return kafkaConsumer.poll(Duration.ofMillis(timeout));
    }

    /**
     * 异步提交offset
     */
    public void commitAsync() {
        kafkaConsumer.commitAsync();
    }

    /**
     * 同步提交offset
     */
    public void commitSync() {
        kafkaConsumer.commitSync();
    }

    public static void main(String[] args) {
        KafkaConsumerClient consumer = new KafkaConsumerClient("test-topic", "test");
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("topic = %s, offset = %d, key = %s, value = %s%n", record.topic(), record.offset(), record.key(), record.value());
        }
    }
}
