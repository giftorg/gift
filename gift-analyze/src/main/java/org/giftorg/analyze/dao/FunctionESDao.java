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
import java.util.*;

@Slf4j
public class FunctionESDao implements Serializable {
    public static final String index = "gift_function";

    public static final String name = "name";

    public static final String source = "source";

    public static final String description = "description";

    public static final String embedding = "embedding";

    public static final String begin = "begin";

    public static final String end = "end";

    public static final String positionLine = "line";

    public static final String positionColumn = "column";

    public static final String language = "language";

    public static final String repoId = "repoId";

    public static final String filePath = "filePath";

    public static final String technologyStack = "technologyStack";

    /**
     * 初始化 function 索引，提供于项目初始化时使用
     */
    public static void init() throws IOException {
        Elasticsearch.EsClient().indices().create(c -> c
                .index(index)
                .mappings(m -> m
                        .properties(name, p -> p.keyword(k -> k))
                        .properties(source, p -> p.keyword(k -> k))
                        .properties(description, p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties(embedding, p -> p.denseVector(k -> k.dims(1024)))
                        .properties(begin, p -> p.object(k -> k
                                .properties(positionLine, f -> f.integer(i -> i.index(false)))
                                .properties(positionColumn, f -> f.integer(i -> i.index(false)))
                        ))
                        .properties(end, p -> p.object(k -> k
                                .properties(positionLine, f -> f.integer(i -> i.index(false)))
                                .properties(positionColumn, f -> f.integer(i -> i.index(false)))
                        ))
                        .properties(language, p -> p.keyword(k -> k))
                        .properties(repoId, p -> p.integer(k -> k))
                        .properties(filePath, p -> p.keyword(k -> k))
                        .properties(technologyStack, p -> p.text(t -> t
                                .analyzer(Elasticsearch.WHITE_SPACE_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                )
        );
    }

    /**
     * 插入代码到 Elasticsearch
     */
    public void insert(Function func) throws IOException {
        log.info("insert function: {}", this);
        Elasticsearch.EsClient().create(c -> c
                .index(index)
                .id(UUID.randomUUID().toString())
                .document(func)
        );
    }

    /**
     * 从 Elasticsearch 检索指定业务代码
     */
    public List<Function> retrieval(String text) throws Exception {
        List<Function> funcs = Elasticsearch.retrieval(index, Arrays.asList(description, technologyStack), text, embedding, Function.class);
        List<Function> result = new ArrayList<>();
        Set<String> sourceSet = new HashSet<>();
        funcs.forEach(func -> {
            if (!sourceSet.contains(func.getSource())) {
                result.add(func);
                sourceSet.add(func.getSource());
            }
        });
        return result;
    }

    public static void main(String[] args) throws Exception {
//        init();
//        testEmbedding("Redis如何设置分布式锁");
        Elasticsearch.close();
    }

    public static void testEmbedding(String text) throws Exception {
        FunctionESDao fd = new FunctionESDao();
        fd.retrieval(text).forEach(f -> log.info(
                "function-{}\n{}\n{}\n" + "```\n{}\n```\n\n\n",
                f.getName(), f.getDescription(), f.getTechnologyStack(), f.getSource()
        ));
    }
}
