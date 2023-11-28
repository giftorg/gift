package org.giftorg.common.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.KeywordProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class IndexTest {
    private static final String serverUrl = "http://elasticsearch:9200";
    private static RestClient restClient;
    private static ElasticsearchClient esClient;

    @BeforeEach
    void setup() {
        // Create the low-level client
        restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        esClient = new ElasticsearchClient(transport);
    }

    @Test
    void testIndex() throws IOException {
//        // 创建索引
//        esClient.indices().create(
//                c -> c.index("es_index").mappings(m -> {
//                    m.properties("text", p -> p.text(t -> t.analyzer("ik_max_word")));
//                    m.properties("title", p -> p.keyword(k -> k));
//                    return m;
//                })
//        );

        // 插入文档
//        esClient.create(c -> c
//                .index("es_index")
//                .id("1")
//                .document(new EsIndex("我是中国人", "你是中国人"))
//        );

//        esClient.search(s -> s
//                        .index("es_index")
//                        .
//                ,
//                EsIndex.class);

    }

    @AfterEach
    void teardown() throws IOException {
        restClient.close();
    }

    @Data
    public static class EsIndex {
        private String text;
        private String title;

        public EsIndex(String text, String title) {
            this.text = text;
            this.title = title;
        }
    }
}
