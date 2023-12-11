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

package org.giftorg.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGLM;
import org.giftorg.common.config.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Elasticsearch 操作类
 */
@Slf4j
public class Elasticsearch {
    private static final String serverUrl = Config.elasticsearchConfig.getHostUrl();
    private static final RestClient restClient;

    // 分词器
    // ik_max_word: 细粒度分词器
    // ik_smart: 粗粒度分词器
    public static final String IK_MAX_WORD_ANALYZER = "ik_max_word";
    public static final String IK_SMART_ANALYZER = "ik_smart";
    public static final String WHITE_SPACE_ANALYZER = "whitespace";

    static {
        restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .build();
    }

    /**
     * 获取 Elasticsearch 客户端单例
     */
    public static ElasticsearchClient EsClient() {
        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    /**
     * 关闭 Elasticsearch 客户端
     */
    public static void close() {
        try {
            restClient.close();
        } catch (Exception e) {
            log.error("rest client close failed: {}", e.getMessage());
        }
    }

    /**
     * 使用向量检索所有文档
     * @param index es 索引
     * @param field es 索引中的向量字段
     * @param embedding 待检索的向量
     * @param type 返回的文档类型
     * @return 检索结果，包含所有文档，按相似度降序排列
     */
    public static <T> List<T> retrieval(String index, String field, List<Double> embedding, Class<T> type) throws IOException {
        SearchResponse<T> resp = EsClient().search(
                search -> search.index(index).query(query -> query
                        .scriptScore(ss -> ss
                                .script(s -> s.inline(l -> l
                                        .source("cosineSimilarity(params.query_vector, '" + field + "') + 1.0")
                                        .params("query_vector", JsonData.of(embedding))
                                ))
                                .query(q -> q.matchAll(ma -> ma))
                        )
                ),
                type
        );

        List<T> result = new ArrayList<>();
        resp.hits().hits().forEach(hit -> {
            result.add(hit.source());
        });

        return result;
    }

    /**
     * 使用指定值过滤，并向量化检索文档
     * @param index es 索引
     * @param textFields es 索引中的文本字段
     * @param text 待检索的文本
     * @param embeddingField es 索引中的向量字段
     * @param type 返回的文档类型
     * @return 检索结果，仅包含过滤后的文档，按相似度降序排列
     */
    public static <T> List<T> retrieval(String index, List<String> textFields, String text, String embeddingField, Class<T> type) throws Exception {
        BigModel model = new ChatGLM();
        List<Double> embedding = model.textEmbedding(text);

        SearchResponse<T> resp = EsClient().search(
                search -> search.index(index).query(query -> query
                        .scriptScore(ss -> ss
                                .script(s -> s.inline(l -> l
                                        .source("cosineSimilarity(params.query_vector, '" + embeddingField + "') + 1.0")
                                        .params("query_vector", JsonData.of(embedding))
                                ))
                                .query(q -> q.multiMatch(mm -> mm
                                        .query(text).
                                        fields(textFields).
                                        operator(Operator.Or)
                                ))
                        )
                ),
                type
        );

        List<T> result = new ArrayList<>();
        resp.hits().hits().forEach(hit -> {
            result.add(hit.source());
        });

        return result;
    }
}
