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

package org.giftorg.scheduler.entity;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGPT;
import org.giftorg.common.utils.CharsetUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Data
@ToString
public class Project {
    private String title; //用于记录项目名称
    private String document;  //用于记录项目文档

    private Integer id;
    private Integer stars;
    private String loginName;
    private String repository;
    private String description;

    private String translation; //用于记录项目的译文
    private List<String> tags;  //用于记录项目的标签
    private boolean isTranslated = false; //用于记录项目是否已经翻译
    private boolean isTagged = false;     //用于记录项目是否已经打标签

    public Project() {
    }

    public Project(String title, String document) {
        this.title = title;
        this.document = document;
    }

    // 翻译项目文档
    public String translation() throws Exception {
        if (isTranslated) return translation;

        if (!CharsetUtil.isChinese(document)) {
            List<BigModel.Message> messages = new ArrayList<>();
            messages.add(new BigModel.Message("system", "Translate the content provided by the user into Chinese.\nInput example: \"hello.\"\nOutput example: \"你好。\""));
            messages.add(new BigModel.Message("user", document));

            BigModel model = new ChatGPT();
            translation = model.chat(messages);
        } else {
            translation = document;
        }

        isTranslated = true;
        return translation;
    }

    // 获取项目关键词列表
    public List<String> tags() throws Exception {
        if (isTagged) return tags;

        List<BigModel.Message> messages = new ArrayList<>();
        messages.add(new BigModel.Message("system", "Based on the user-inputted project documents, generate a list of Chinese keywords related to the project. Provide the answer in the format of comma-separated English keywords.\nAnswer example: \"Java,MySQL,Redis,管理系统,电商\"\n"));
        messages.add(new BigModel.Message("user", document));

        BigModel model = new ChatGPT();
        String answer = model.chat(messages);
        tags = Arrays.asList(answer.split(",\\s*"));
        isTagged = true;
        return tags;
    }
}