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

package org.giftorg.analyze.dao;

import lombok.extern.slf4j.Slf4j;
import org.giftorg.analyze.entity.Repository;
import org.giftorg.common.elasticsearch.Elasticsearch;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

@Slf4j
public class RepositoryESDao implements Serializable {
    public static void init() throws IOException {
        Elasticsearch.EsClient().indices().create(c -> c
                .index("gift_repository")
                .mappings(m -> m
                        .properties("repoId", p -> p.integer(k -> k))
                        .properties("name", p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                        ))
                        .properties("fullName", p -> p.keyword(k -> k))
                        .properties("stars", p -> p.integer(k -> k
                                .index(false)
                        ))
                        .properties("author", p -> p.keyword(k -> k))
                        .properties("url", p -> p.keyword(k -> k))
                        .properties("description", p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties("size", p -> p.integer(k -> k
                                .index(false)
                        ))
                        .properties("defaultBranch", p -> p.keyword(k -> k
                                .index(false)
                        ))
                        .properties("readme", p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties("readmeCn", p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties("tags", p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties("hdfsPath", p -> p.keyword(k -> k
                                .index(false)
                        ))
                )
        );
    }

    public void insert(Repository repo) throws IOException {
        log.info("insert repository: {}", this);
        Elasticsearch.EsClient().create(c -> c
                .index("gift_repository")
                .id(UUID.randomUUID().toString())
                .document(repo)
        );
    }

    public static void main(String[] args) throws Exception {
        init();
        Elasticsearch.close();
    }
}
