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

package org.giftorg.backed.dao;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import lombok.extern.slf4j.Slf4j;
import org.giftorg.backed.entity.repository.Repository;
import org.giftorg.common.elasticsearch.Elasticsearch;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class RepositoryESDao implements Serializable {
    public static final String index = "gift_repository";

    public static final String repoId = "repoId";

    public static final String name = "name";

    public static final String fullName = "fullName";

    public static final String stars = "stars";

    public static final String author = "author";

    public static final String url = "url";

    public static final String description = "description";

    public static final String size = "size";

    public static final String defaultBranch = "defaultBranch";

    public static final String readme = "readme";

    public static final String readmeCn = "readmeCn";

    public static final String tags = "tags";

    public static final String hdfsPath = "hdfsPath";

    /**
     * 初始化 repository 索引，提供于项目初始化时使用
     */
    public static void init() throws IOException {
        Elasticsearch.EsClient().indices().create(c -> c
                .index(index)
                .mappings(m -> m
                        .properties(repoId, p -> p.integer(k -> k))
                        .properties(name, p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                        ))
                        .properties(fullName, p -> p.keyword(k -> k))
                        .properties(stars, p -> p.integer(k -> k
                                .index(false)
                        ))
                        .properties(author, p -> p.keyword(k -> k))
                        .properties(url, p -> p.keyword(k -> k))
                        .properties(description, p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties(size, p -> p.integer(k -> k
                                .index(false)
                        ))
                        .properties(defaultBranch, p -> p.keyword(k -> k
                                .index(false)
                        ))
                        .properties(readme, p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties(readmeCn, p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties(tags, p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties(hdfsPath, p -> p.keyword(k -> k
                                .index(false)
                        ))
                )
        );
    }

    /**
     * 插入仓库信息到 Elasticsearch
     */
    public void insert(Repository repo) throws IOException {
        log.info("insert repository: {}", this);
        Elasticsearch.EsClient().create(c -> c
                .index(index)
                .id(UUID.randomUUID().toString())
                .document(repo)
        );
    }

    /**
     * 查询仓库
     */
    public List<Repository> retrieval(String text) throws Exception {
        List<Repository> repositories = new ArrayList<>();
        Elasticsearch.EsClient().search(
                search -> search.index(index).query(query -> query
                        .multiMatch(m -> m
                                .query(text)
                                .fields(name, description, readme, readmeCn, tags)
                                .operator(Operator.Or)
                        )
                ),
                Repository.class
        ).hits().hits().forEach(r -> {
            repositories.add(r.source());
        });
        return repositories;
    }

    public static void main(String[] args) throws Exception {
        init();
        testRetrievalResponse("elasticsearch");
        Elasticsearch.close();
    }

    public static void testRetrievalResponse(String text) throws Exception {
        RepositoryESDao rd = new RepositoryESDao();
        List<Repository> repositories = rd.retrieval(text);
        repositories.forEach(System.out::println);
    }
}
