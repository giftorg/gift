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

package org.giftorg.common.config;

import lombok.Data;

import java.util.List;

/**
 * 配置的结构
 */
public class Properties {

    public SparkProperties spark;
    public HDFSProperties hdfs;
    public XingHouProperties xingHou;
    public ChatGPTProperties chatGPT;
    public ChatGLMProperties chatGLM;
    public KafkaProperties kafka;
    public ElasticsearchProperties elasticsearch;

    @Data
    public static class SparkProperties {
        private String master;
    }

    @Data
    public static class HDFSProperties {
        private String addr;
        private String reposPath;
    }

    @Data
    public static class XingHouProperties {
        private String hostUrl;

        private String appid;

        private String apiSecret;

        private String apiKey;
    }

    @Data
    public static class ChatGPTProperties {
        private String host;

        private List<String> apiKeys;

        private String model;
    }

    @Data
    public static class ChatGLMProperties {
        private String hostUrl;

        private String apiKey;
    }

    @Data
    public static class KafkaProperties {
        private String hostUrl;
    }

    @Data
    public static class ElasticsearchProperties {
        private String hostUrl;
    }
}
