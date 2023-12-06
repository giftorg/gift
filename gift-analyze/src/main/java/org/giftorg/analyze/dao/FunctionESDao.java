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
import org.giftorg.analyze.entity.Function;
import org.giftorg.common.elasticsearch.Elasticsearch;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Slf4j
public class FunctionESDao implements Serializable {
    public static void init() throws IOException {
        Elasticsearch.EsClient().indices().create(c -> c
                .index("gift_function")
                .mappings(m -> m
                        .properties("name", p -> p.keyword(k -> k))
                        .properties("source", p -> p.keyword(k -> k))
                        .properties("description", p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties("embedding", p -> p.denseVector(k -> k.dims(1024)))
                        .properties("begin", p -> p.object(k -> k
                                .properties("line", f -> f.integer(i -> i.index(false)))
                                .properties("column", f -> f.integer(i -> i.index(false)))
                        ))
                        .properties("end", p -> p.object(k -> k
                                .properties("line", f -> f.integer(i -> i.index(false)))
                                .properties("column", f -> f.integer(i -> i.index(false)))
                        ))
                        .properties("language", p -> p.keyword(k -> k))
                        .properties("repoId", p -> p.integer(k -> k))
                        .properties("filePath", p -> p.keyword(k -> k))
                )
        );
    }

    public void insert(Function func) throws IOException {
        log.info("insert function: {}", this);
        Elasticsearch.EsClient().create(c -> c
                .index("gift_function")
                .id(UUID.randomUUID().toString())
                .document(func)
        );
    }

    public List<Function> retrieval(String text) throws Exception {
        return Elasticsearch.retrieval("gift_function", "description", text, "embedding", Function.class);
    }

    public static void main(String[] args) throws Exception {
        init();
        Elasticsearch.close();
    }
}
