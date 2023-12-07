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
 * 函数实体
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
     * 函数分析与向量化
     */
    public void analyze() {
        // 获取函数的描述信息
        BigModel gpt = new ChatGPT();
        try {
            description = gpt.chat(new ArrayList<BigModel.Message>() {{
                // 为用户输入的函数写一行不超过50字的中文注释，描述函数的作用。
                // Write a Chinese comment in one line, not exceeding 50 characters, for the user-inputted function, describing the function's purpose.
                add(new BigModel.Message("system", "Write a Chinese comment in one line, not exceeding 50 characters, for the user-inputted function, describing the function's purpose.\nInput example: \"public static void add(int a, int b) { return a + b; }\"\nOutput example: \"计算两位整数的和\""));
                add(new BigModel.Message("user", source));
            }});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 将函数描述向量化
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
