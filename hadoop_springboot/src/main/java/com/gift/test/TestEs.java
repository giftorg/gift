/*
package com.gift.test;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TestEs {

    @Autowired
    private ElasticsearchClient client;

    public void createTest() throws IOException {

        //写法比RestHighLevelClient更加简洁
        CreateIndexResponse indexResponse = client.indices().create(c -> c.index("user"));
    }
}
*/
