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

package org.giftorg.analyze;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.giftorg.analyze.dao.FunctionESDao;
import org.giftorg.analyze.entity.AnalyzeTask;
import org.giftorg.analyze.entity.Repository;
import org.giftorg.common.elasticsearch.Elasticsearch;
import org.giftorg.common.kafka.KafkaConsumerClient;
import org.giftorg.common.kafka.KafkaProducerClient;
import org.giftorg.common.tokenpool.TokenPool;

@Slf4j
public class AnalyzeApplication {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("GiftAnalyzer");
        SparkContext sc = new SparkContext(conf);

        KafkaConsumerClient consumer = new KafkaConsumerClient(AnalyzeTask.TOPIC, "gift");
        KafkaProducerClient producer = new KafkaProducerClient();
        Analyzer analyzer = new Analyzer(sc);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            sc.stop();
            Elasticsearch.close();
            TokenPool.closeDefaultTokenPool();
        }));

        while (true) {
            consumer.poll(100).forEach(record -> {
                consumer.commitAsync();

                log.info("receive analyze task: {}, offset: {}", record.value(), record.offset());
                String jsonVal = record.value();
                AnalyzeTask analyzeTask = JSONUtil.toBean(jsonVal, AnalyzeTask.class);
                try {
                    // 获取并分析仓库
                    Repository repository = analyzeTask.getProject();
                    analyzer.run(repository);
                } catch (Exception e) {
                    // 任务处理失败，重试
                    analyzeTask.retry(producer);
                    log.error("analyze repository error: {}", e.getMessage());
                }
            });
        }
    }

    public static void testEmbedding(String query) {
        FunctionESDao fd = new FunctionESDao();
        try {
            fd.retrieval(query).forEach(System.out::println);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Elasticsearch.close();
    }
}
