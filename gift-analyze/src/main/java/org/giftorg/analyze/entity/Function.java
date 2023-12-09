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

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGLM;
import org.giftorg.common.bigmodel.impl.ChatGPT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * 技术栈列表，以空格分隔
     */
    private String technologyStack;

    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /* prompt
    分析用户输入的Java方法代码，回答一个json字符串。
    json结构包含3个字段：
    1. name: 方法的名称；
    2. desc: 采用中文描述方法处理的业务，30到50字/词；
    3. techs：与该方法相关的搜索词列表，以中文为主，术语可用英文，多个单词拆分成多个元素，搜索词列表要尽量全面。
    回答示例：
    {
      "name": "retrieval",
      "desc": "通过指定值过滤并向量化检索文档，实现基于向量化的文档相似度查询",
      "techs": ["elasticsearch", "向量化", "检索", "dense", "vector", "相似度", "检索", "文本", "文本匹配"]
    }
     */
    private static final String FUNCTION_ANALYZE_PROMPT = "Analyze Java method code provided by the user and respond with a JSON string. The JSON structure includes three fields:\n" +
            "1. name: The name of the method;\n" +
            "2. desc: A Chinese description (30 to 50 characters/words) of the business process handled by the method;\n" +
            "3. techs: A list of search terms related to the method, primarily in Chinese with English terms allowed. If a term consists of multiple words, break them into separate elements. The search term list should be as comprehensive as possible.\n" +
            "Example response:\n" +
            "{\n" +
            "  \"name\": \"retrieval\",\n" +
            "  \"desc\": \"通过指定值过滤并向量化检索文档，实现基于向量化的文档相似度查询\",\n" +
            "  \"techs\": [\"elasticsearch\", \"向量化\", \"检索\", \"dense\", \"vector\", \"相似度\", \"检索\", \"文本\", \"文本匹配\"]\n" +
            "}";

    /**
     * 代码分析与向量化
     */
    public Boolean analyze() {
        // 获取函数的描述信息
        BigModel gpt = new ChatGPT();
        try {
            description = gpt.chat(new ArrayList<BigModel.Message>() {{
                add(new BigModel.Message("user", source));
                add(new BigModel.Message("system", FUNCTION_ANALYZE_PROMPT));
            }});
        } catch (Exception e) {
            throw new RuntimeException("ChatGPT call failed: " + e);
        }
        if (description.isEmpty()) {
            throw new RuntimeException("ChatGPT call failed, empty response.");
        }

        // 解析代码结果
        FunctionAnalyzeResult result;
        try {
            result = JSONUtil.toBean(description, FunctionAnalyzeResult.class);
        } catch (Exception ignored) {
            throw new RuntimeException("function analysis failed and the ChatGPT response failed to be parsed.");
        }
        technologyStack = String.join(" ", result.getTechs());
        description = result.getDesc();

        // 生成文本嵌入向量
        BigModel glm = new ChatGLM();
        try {
            embedding = glm.textEmbedding(description);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
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
