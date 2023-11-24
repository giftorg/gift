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

package org.giftorg.spark.config;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 读取与存储项目配置
 */
@Slf4j
public class Config {
    public static Properties.HDFSProperties hdfsConfig;
    public static Properties.XingHouProperties xingHouConfig;
    public static Properties.ChatGPTProperties chatGPTConfig;

    static {
        Yaml yaml = new Yaml();
        Properties config;
        URL url = ClassLoader.getSystemResource("config.yaml");

        try (InputStream in = Files.newInputStream(Paths.get(url.toURI()))) {
            config = yaml.loadAs(in, Properties.class);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        hdfsConfig = config.hdfs;
        xingHouConfig = config.xingHou;
        chatGPTConfig = config.chatGPT;
    }
}
