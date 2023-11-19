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
import java.util.List;

class Project {
    private String docContent;
    private String projectName;
    private List<String> tags;

    private String translation;

    public Project(String docContent, String projectName) {
        this.docContent = docContent;
        this.projectName = projectName;
        this.tags = new ArrayList<>();
    }

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

    public String translateToProject(String doc) throws Exception {
        // 在这里编写使用星火大模型分析文档内容，获取标签列表的代码逻辑
        BigModelNew model = new BigModelNew();// 假设已经实现了一个名为StarfireModel的类
        String tanslation = model.modelTranslate(this.docContent);    //其中有一个analyze方法用于分析文档内容
        return tanslation;
    }

    public ArrayList<String> taggingToProject(String translation) throws Exception {
        // 在这里编写使用星火大模型分析文档内容，获取标签列表的代码逻辑
        BigModelNew model = new BigModelNew();// new一个名为model的BigModelNew类
        ArrayList<String> tags = model.modelTags(this.getTranslation());    //其中有一个analyze方法用于分析文档内容
        return tags;
    }

}