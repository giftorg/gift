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

package org.giftorg.analyze.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.giftorg.common.kafka.RetryTask;

@EqualsAndHashCode(callSuper = true)
@Data
public class AnalyzeTask extends RetryTask {
    private Repository project;

    private Integer retryCount;

    public static final Integer MAX_RETRY_COUNT = 3;

    public static final String TOPIC = "gift-analyze";

    public AnalyzeTask(Repository project) {
        this.project = project;
        this.retryCount = RetryTask.DEFAULT_RETRY_COUNT;
    }

    public boolean isRetry() {
        return retryCount++ < MAX_RETRY_COUNT;
    }

    @Override
    public String topic() {
        return TOPIC;
    }

}
