# Copyright 2023 GiftOrg Authors
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import json
import logging

from kafka import KafkaProducer

DEFAULT_RETRY_COUNT = 0
MAX_RETRY_COUNT = 3
CRAWLER_TOPIC = "gift-crawler"


class CrawlerTask:
    def __init__(self, repo_id: int, retry_count: int):
        self.repo_id = repo_id
        self.retry_count = retry_count


class CrawlerMQ:
    def __init__(self, server: str):
        self.producer = KafkaProducer(
            bootstrap_servers=[server],
            value_serializer=lambda val: json.dumps(val).encode()
        )

    def publish(self, topic: str, task: CrawlerTask):
        try:
            self.producer.send(topic=topic, value={
                'repoId': task.repo_id,
                'retryCount': task.retry_count
            }).get(timeout=100)
            logging.info(f'task(repo_id={task.repo_id}) 发布成功')
        except Exception as e:
            logging.error(f"task(repo_id={task.repo_id}) 发布失败：{e}，重试次数：{task.retry_count}")
            if task.retry_count < MAX_RETRY_COUNT:
                task.retry_count += 1
                self.publish(topic, task)
