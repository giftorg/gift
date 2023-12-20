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

package org.giftorg.backed.entity.vo;

import lombok.Data;
import org.giftorg.backed.entity.repository.Project;
import org.giftorg.backed.entity.repository.Repository;
import org.giftorg.backed.entity.code.Position;

/**
 * 返回前端
 */
@Data
public class FunctionVo {
    private String name;    //项目名

    private String source;  //源代码

    private String description;     //描述

    private Position begin; //表示代码中的位置信息，例如行号和列号

    private Position end;   //表示代码中的位置信息

    private String language;    //语言

    private Integer repoId;     //仓库ID

    private String filePath;     //URL

    private String technologyStack; //所用的技术

    private Project project;
}
