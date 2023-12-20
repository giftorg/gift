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

package org.giftorg.backed.entity.repository;

import lombok.Data;

import java.util.List;

@Data
public class Repository {
    private Integer id;

    private Integer repoId;

    private String name;

    private String fullName;

    private Integer stars;

    private String author;

    private String url;

    private String description;

    private Integer size;

    private String defaultBranch;

    private String readme;

    private String readmeCn;

    private List<String> tags;

    private String hdfsPath;

    private boolean isTranslated = false;

    private boolean isTagged = false;
}
