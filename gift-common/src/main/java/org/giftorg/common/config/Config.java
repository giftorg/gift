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

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkFiles;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 初始化并存储项目配置
 */
@Slf4j
public class Config {
    private static final String DEFAULT_CONFIG_PATH = "config.yaml";

    public static Properties.HDFSProperties hdfsConfig;
    public static Properties.XingHouProperties xingHouConfig;
    public static Properties.ChatGPTProperties chatGPTConfig;
    public static Properties.ChatGLMProperties chatGLMConfig;
    public static Properties.KafkaProperties kafkaConfig;
    public static Properties.ElasticsearchProperties elasticsearchConfig;

    // 初始化项目配置
    static {
        log.info("Initializing configuration ...");

        String configPath = DEFAULT_CONFIG_PATH;
        boolean isSpark = true;
        try {
            configPath = SparkFiles.get(DEFAULT_CONFIG_PATH);
        } catch (Exception e) {
            isSpark = false;
        }

        InputStream in;
        if (isSpark && new File(configPath).exists()) {
            try {
                in = Files.newInputStream(Paths.get(configPath));
                log.info("config path: {}", configPath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            configPath = DEFAULT_CONFIG_PATH;

            try {
                URI uri = ClassLoader.getSystemResource(configPath).toURI();
                in = Files.newInputStream(Paths.get(uri));
                log.info("config path: {}", uri.getPath());
            } catch (Exception ignore) {
                try {
                    URI uri = ClassLoader.getSystemResource(configPath).toURI();
                    Map<String, String> env = new HashMap<>();
                    env.put("create", "true");
                    FileSystems.newFileSystem(uri, env);
                    in = Files.newInputStream(Paths.get(uri));
                    log.info("config path: {}", uri.getPath());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Properties config;
        try {
            config = new Yaml().loadAs(in, Properties.class);
            in.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        hdfsConfig = config.hdfs;
        xingHouConfig = config.xingHou;
        chatGPTConfig = config.chatGPT;
        chatGLMConfig = config.chatGLM;
        kafkaConfig = config.kafka;
        elasticsearchConfig = config.elasticsearch;
    }
}
