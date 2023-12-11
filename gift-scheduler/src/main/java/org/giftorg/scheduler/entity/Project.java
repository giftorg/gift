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
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 项目实体类
 */
@Slf4j
@Data
@ToString
public class Project {
    /**
     * MySQL 记录 ID
     */
    private Integer id;

    /**
     * 仓库 ID
     */
    private Integer repoId;

    /**
     * 仓库名称
     */
    private String name;

    /**
     * 仓库全名
     */
    private String fullName;

    /**
     * 仓库 star 数
     */
    private Integer stars;

    /**
     * 仓库作者/组织
     */
    private String author;

    /**
     * 仓库完整路径
     */
    private String url;

    /**
     * 仓库描述
     */
    private String description;

    /**
     * 仓库文件大小
     */
    private Integer size;

    /**
     * Git 默认分支
     */
    private String defaultBranch;

    /**
     * README.md 文档
     */
    private String readme;

    /**
     * README.md 文档中文翻译
     */
    private String readmeCn;

    /**
     * 仓库关键词列表
     */
    private List<String> tags;

    /**
     * 仓库保存在 HDFS 中的路径
     */
    public String hdfsPath;
}
