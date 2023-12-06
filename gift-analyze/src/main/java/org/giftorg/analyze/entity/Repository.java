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

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGPT;
import org.giftorg.common.utils.CharsetUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ToString
public class Repository implements Serializable, Cloneable {
    public Integer id;

    public Integer repoId;

    public String name;

    public String fullName;

    public Integer stars;

    public String author;

    public String url;

    public String description;

    public Integer size;

    public String defaultBranch;

    public String readme;

    public String readmeCn;

    public List<String> tags;

    public String hdfsPath;

    private boolean isTranslated = false;

    private boolean isTagged = false;

    public Repository() {
    }

    public Repository(String name, String readme) {
        this.name = name;
        this.readme = readme;
    }

    @Override
    public Repository clone() throws CloneNotSupportedException {
        return (Repository) super.clone();
    }

    /**
     * 翻译项目文档
     */
    public void translation() throws Exception {
        if (isTranslated) return;

        if (!CharsetUtil.isChinese(readmeCn)) {
            List<BigModel.Message> messages = new ArrayList<>();
            messages.add(new BigModel.Message("system", "Translate the content provided by the user into Chinese.\nInput example: \"hello.\"\nOutput example: \"你好。\""));
            messages.add(new BigModel.Message("user", readme));

            BigModel model = new ChatGPT();
            readmeCn = model.chat(messages);
        } else {
            readmeCn = readmeCn;
        }

        isTranslated = true;
    }

    /**
     * 获取项目关键词列表
     */
    public void tagging() throws Exception {
        if (isTagged) return;

        List<BigModel.Message> messages = new ArrayList<>();
        messages.add(new BigModel.Message("system", "Based on the user-inputted project documents, generate a list of Chinese keywords related to the project. Provide the answer in the format of comma-separated English keywords.\nAnswer example: \"Java,MySQL,Redis,管理系统,电商\"\n"));
        messages.add(new BigModel.Message("user", readme));

        BigModel model = new ChatGPT();
        String answer = model.chat(messages);
        tags = Arrays.asList(answer.split(",\\s*"));
        isTagged = true;
    }
}