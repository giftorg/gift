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

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaProducerClient {

    Producer<String, String> kafkaProducer;

    public KafkaProducerClient() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        props.put("batch.size", 10);
        props.put("linger.ms", 100);

        kafkaProducer = new KafkaProducer<>(props);
    }

    public Future<RecordMetadata> send(ProducerRecord<String, String> producerRecord) {
        return kafkaProducer.send(producerRecord);
    }

    public Future<RecordMetadata> send(ProducerRecord<String, String> producerRecord, Callback callback) {
        return kafkaProducer.send(producerRecord, callback);
    }

    public static void main(String[] args) {
        KafkaProducerClient producer = new KafkaProducerClient();

        for (int i = 0; i < 20; i++) {
            Future<RecordMetadata> result = producer.send(new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i)));
            try {
                RecordMetadata metadata = result.get();
                System.out.println(metadata);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
