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

package org.giftorg.spark;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Project {
    private String docContent;  //用于记录项目文档
    private String projectName; //用于记录项目名称
    private List<String> tags;  //用于记录项目的标签

    private String translation; //用于记录项目的译文

    public Project(String docContent, String projectName) { //构造方法
        this.docContent = docContent;
        this.projectName = projectName;
        this.tags = new ArrayList<>();
    }
        //以下是对应属性的get，set方法
    public String getDocContent() {
        return docContent;
    }

    public void setDocContent(String docContent) {
        this.docContent = docContent;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String translateToProject(String doc) throws Exception { // 在这里编写使用星火大模型分析文档内容，获取标签列表的代码逻辑
        BigModelNew model = new BigModelNew();// 假设已经实现了一个名为StarfireModel的类
        String answer = model.chat("不要有多余的废话！请你帮我将冒号后的文本（除了术语或者技术之外）" +
                "翻译为中文，如果本身就是中文的话，就原样输出即可："+this.docContent);    ////发送一个问题，返回一个字符串类型的答案
        return answer;//返回回答（翻译内容）
    }

    public ArrayList<String> taggingToProject(String translation) throws Exception { // 在这里编写使用星火大模型分析文档内容，获取标签列表的代码逻辑
        BigModelNew model = new BigModelNew();// new一个名为model的BigModelNew类
        String answer = model.chat("不要有多余的废话！" +
                "请你帮我将冒号后的文本中所有用到的技术和你任务的关键词选出来，并以英文逗号分割：" + this.getTranslation());
            //发送一个问题，返回一个字符串类型的答案
        //……如果获得的answer不满意可以在此处接着添加代码，用第三方模块进行筛选，如jieba等
        ArrayList<String> tags = new ArrayList<>(Arrays.asList(answer.split(",")));
        return tags;    //返回标签
    }

}