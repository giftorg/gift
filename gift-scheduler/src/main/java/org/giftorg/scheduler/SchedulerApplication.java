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

package org.giftorg.scheduler;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.giftorg.common.kafka.KafkaConsumerClient;
import org.giftorg.common.kafka.KafkaProducerClient;
import org.giftorg.common.kafka.RetryCallback;
import org.giftorg.scheduler.entity.AnalyzeTask;
import org.giftorg.scheduler.entity.CrawlerTask;
import org.giftorg.scheduler.entity.Project;
import org.giftorg.scheduler.service.ProjectService;
import org.giftorg.scheduler.service.impl.ProjectServiceImpl;

@Slf4j
public class SchedulerApplication {
    private static final ProjectService ps = new ProjectServiceImpl();
    public static final Integer MAX_PROJECT_SIZE = 1024;

    public static void main(String[] args) {
        KafkaConsumerClient crawlerConsumer = new KafkaConsumerClient(CrawlerTask.TOPIC, "gift");
        KafkaProducerClient producer = new KafkaProducerClient();

        while (true) {
            crawlerConsumer.poll(100).forEach(record -> {
                // 有完善的重试策略，只需要保证正常提交
                crawlerConsumer.commitAsync();

                log.info("receive crawler task: {}, offset: {}", record.value(), record.offset());
                CrawlerTask crawlerTask = JSONUtil.toBean(record.value(), CrawlerTask.class);
                try {
                    // 查询项目信息
                    Project project = ps.getProjectByRepoId(crawlerTask.getRepoId());
                    if (project.getSize() > MAX_PROJECT_SIZE) {
                        log.warn("project {} size is too large, skip", project.getFullName());
                    } else {
                        log.info("get project {} success", project.getFullName());

                        // 克隆仓库并上传HDFS
                        ps.pullProject(project);
                        log.info("clone and put project {} success", project.getFullName());

                        // 发布仓库分析任务到Kafka
                        AnalyzeTask analyzeTask = new AnalyzeTask(project);
                        String jsonStr = JSONUtil.toJsonStr(analyzeTask);
                        ProducerRecord<String, String> msg = new ProducerRecord<>(AnalyzeTask.TOPIC, jsonStr);
                        producer.send(msg, new RetryCallback(producer, analyzeTask));
                    }
                } catch (Exception e) {
                    // 任务处理失败，重试
                    log.error("clone or put project {} failed", e.getMessage());
                    crawlerTask.retry(producer);
                }
            });
        }
    }
}
