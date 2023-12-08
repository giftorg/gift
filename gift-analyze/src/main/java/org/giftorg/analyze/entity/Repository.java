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
import org.giftorg.common.bigmodel.impl.XingHuo;
import org.giftorg.common.tokenpool.TokenPool;
import org.giftorg.common.utils.CharsetUtil;
import org.giftorg.common.utils.MarkdownUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ToString
public class Repository implements Serializable, Cloneable {
    private final BigModel gpt = new ChatGPT();

    private final BigModel xingHuo = new XingHuo();

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

    private static final int CHUNK_SIZE = 1000;

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
        if (!CharsetUtil.isChinese(readme)) {
//            List<BigModel.Message> messages = new ArrayList<>();
            List<BigModel.Message> messages = readmeContentMessages();
//            messages.add(new BigModel.Message("user", readme));
            messages.add(new BigModel.Message("user", "去除以上文档中任何链接、许可等无关内容，只保留核心文本描述，使用中文概括上面的内容。"));
            readmeCn = xingHuo.chat(messages);
            // TODO: 添加更优的判断方式
            // TODO: 根据星火响应的特征，失败时会响应 “很抱歉，您没有提供任何文档或链接供我删除无关内容。请提供相关文档或链接，我将为您删除其中的无关内容并使用中文概括核心文本描述。”
            if (readmeCn.contains("抱歉")) throw new Exception("translation failed, xinghuo response: " + readmeCn);
        } else {
            readmeCn = readme;
        }
        isTranslated = true;
    }

    /**
     * 获取项目关键词列表
     */
    public void tagging() throws Exception {
        if (isTagged) return;
        List<BigModel.Message> messages = new ArrayList<>();
        // TODO: 添加是否已翻译判断？
        messages.add(new BigModel.Message("user", readmeCn));
        messages.add(new BigModel.Message("system", "Based on the user-inputted project documents, generate a list of Chinese keywords related to the project. Provide the answer in the format of comma-separated English keywords.\nAnswer example: \"Java,MySQL,Redis,管理系统,电商\"\n"));
        String answer = gpt.chat(messages);
        tags = Arrays.asList(answer.split(",\\s*"));
        isTagged = true;
    }

    public List<BigModel.Message> readmeContentMessages() {
        List<BigModel.Message> messages = new ArrayList<>();
        List<String> chunks = splitReadmeContent();
        for (String chunk : chunks) {
            messages.add(new BigModel.Message("user", chunk));
        }
        return messages;
    }

    public List<String> splitReadmeContent() {
        String content = MarkdownUtil.extractText(readme);
        ;
        System.out.println(content);
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i <= content.length() / CHUNK_SIZE; i++) {
            chunks.add(content.substring(i * CHUNK_SIZE, Math.min(content.length(), (i + 1) * CHUNK_SIZE)));
        }
        return chunks;
    }

    public static void main(String[] args) throws Exception {
        Repository repository = new Repository("test", "hello world.");
        repository.translation();
        log.info("readmeCn: {}", repository.readmeCn);
        TokenPool.closeDefaultTokenPool();
    }
}