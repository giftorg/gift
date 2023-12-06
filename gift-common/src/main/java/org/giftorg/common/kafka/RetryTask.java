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

import cn.hutool.json.JSONUtil;
import org.apache.kafka.clients.producer.ProducerRecord;

public abstract class RetryTask {

    public static Integer DEFAULT_RETRY_COUNT = 0;

    /**
     * 判断是否需要重试，实现类在此处维护重试次数
     */
    public abstract boolean isRetry();

    public abstract String topic();

    public void retry(KafkaProducerClient producer) {
        if (isRetry()) {
            ProducerRecord<String, String> msg = new ProducerRecord<>(topic(), JSONUtil.toJsonStr(this));
            producer.send(msg, new RetryCallback(producer, this));
        }
    }
}
