package org.giftorg.common.elasticsearch;

import cn.hutool.json.JSONUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.json.stream.JsonGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class Elasticsearch {
    private static final String serverUrl = "http://elasticsearch:9200";
    private static final RestClient restClient;
    private static final ElasticsearchClient esClient;

    public static final String IK_MAX_WORD_ANALYZER = "ik_max_word";
    public static final String IK_SMART_ANALYZER = "ik_smart";

    static {
        restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper());
        esClient = new ElasticsearchClient(transport);
    }

    public static ElasticsearchClient EsClient() {
        return esClient;
    }

    public static void close() throws IOException {
        restClient.close();
    }

    public static <T> List<T> retrieval(String index, String field, List<Double> embedding, Class<T> type) throws IOException {
        SearchResponse<T> resp = esClient.search(
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
}
