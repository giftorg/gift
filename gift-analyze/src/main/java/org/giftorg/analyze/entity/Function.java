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
import lombok.extern.slf4j.Slf4j;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGLM;
import org.giftorg.common.bigmodel.impl.ChatGPT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码实体
 */
@Data
@Slf4j
public class Function implements Serializable {
    private String name;

    private String source;

    private String description;

    private List<Double> embedding;

    private Position begin;

    private Position end;

    private String language;

    private Integer repoId;

    private String filePath;

    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    /**
     * 代码分析与向量化
     */
    public void analyze() {
        // 获取函数的描述信息
        BigModel gpt = new ChatGPT();
        try {
            description = gpt.chat(new ArrayList<BigModel.Message>() {{
                /* prompt:
                    分析用户输入的Java类代码，回答一个json字符串。描述类中核心方法的作用。
                    外层包含两个字段：
                    1. methods，类中的核心方法列表。
                    2. techs：类中涉及到的技术名词列表。
                    每个方法包含3个字段：
                    1. name: 方法的名称；
                    2. desc: 采用中文描述方法的作用，不超过50字；
                    3. techs：方法使用到的技术名词列表。
                    回答示例：
                    {
                      "methods": [
                        {
                          "name": "EsClient",
                          "desc": "返回Elasticsearch客户端单例",
                          "techs": [ "RestClient", "JacksonJsonpMapper" ]
                        },
                        {
                          "name": "retrieval",
                          "desc": "通过指定值过滤并向量化检索文档",
                          "techs": [ "scriptScore", "cosineSimilarity" ]
                        }
                      ],
                      "techs": ["Elasticsearch", "Java", "RestClient", "JacksonJsonpMapper", "scriptScore", "cosineSimilarity", "向量化", "检索"]
                    }
                 */
                String prompt = "Analyze user-input Java class code and generate a JSON string answering the description of core methods in the class. The outer structure includes two fields:\n" +
                        "1. methods: a list of core methods in the class.\n" +
                        "2. techs: a list of technical terms involved in the class.\n" +
                        "Each method consists of three fields:\n" +
                        "1. name: the name of the method.\n" +
                        "2. desc: a Chinese description of the method's function in no more than 50 characters.\n" +
                        "3. techs: a list of technical terms used by the method.\n" +
                        "Example response:\n" +
                        "{\n" +
                        "  \"methods\": [\n" +
                        "    {\n" +
                        "      \"name\": \"EsClient\",\n" +
                        "      \"desc\": \"返回Elasticsearch客户端单例\",\n" +
                        "      \"techs\": [ \"RestClient\", \"JacksonJsonpMapper\" ]\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"name\": \"retrieval\",\n" +
                        "      \"desc\": \"通过指定值过滤并向量化检索文档\",\n" +
                        "      \"techs\": [ \"scriptScore\", \"cosineSimilarity\" ]\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"techs\": [\"Elasticsearch\", \"Java\", \"RestClient\", \"JacksonJsonpMapper\", \"scriptScore\", \"cosineSimilarity\", \"向量化\", \"检索\"]\n" +
                        "}";
//                add(new BigModel.Message("system", "Write a Chinese comment in one line, not exceeding 50 characters, for the user-inputted function, describing the function's purpose.\nInput example: \"public static void add(int a, int b) { return a + b; }\"\nOutput example: \"计算两位整数的和\""));
                add(new BigModel.Message("user", source));
                add(new BigModel.Message("system", prompt));
            }});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 将代码描述向量化
        BigModel glm = new ChatGLM();
        try {
            embedding = glm.textEmbedding(description);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Function{" +
                "name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", description='" + description + '\'' +
                ", embedding=" + embedding +
                ", begin=" + begin +
                ", end=" + end +
                ", language='" + language + '\'' +
                ", repoId=" + repoId +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
