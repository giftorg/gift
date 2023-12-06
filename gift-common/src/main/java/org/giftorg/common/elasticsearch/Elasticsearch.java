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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Elasticsearch {
    private static final String serverUrl = "http://elasticsearch:9200";
    private static final RestClient restClient;

    public static final String IK_MAX_WORD_ANALYZER = "ik_max_word";
    public static final String IK_SMART_ANALYZER = "ik_smart";

    static {
        restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .build();
    }

    public static ElasticsearchClient EsClient() {
        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    public static void close() {
        try {
            restClient.close();
        } catch (Exception e) {
            log.error("rest client close failed: {}", e.getMessage());
        }
    }

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

    public static <T> List<T> retrieval(String index, String textField, String text, String embeddingField, Class<T> type) throws Exception {
        BigModel model = new ChatGLM();
        List<Double> embedding = model.textEmbedding(text);

        SearchResponse<T> resp = EsClient().search(
                search -> search.index(index).query(query -> query
                        .scriptScore(ss -> ss
                                .script(s -> s.inline(l -> l
                                        .source("cosineSimilarity(params.query_vector, '" + embeddingField + "') + 1.0")
                                        .params("query_vector", JsonData.of(embedding))
                                ))
                                .query(q -> q.match(ma -> ma.field(textField).query(text)))
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
