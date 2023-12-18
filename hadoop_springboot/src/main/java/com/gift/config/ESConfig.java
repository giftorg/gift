/*package com.gift.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

*//**
 * 初始化ES客户端
 *//*
@Configuration
public class ESConfig {

    @Bean
    public void initEs(){
        HttpHost httpHost = HttpHost.create("http://elasticsearch:9200");
        RestClientBuilder builder = RestClient.builder(httpHost);
        RestHighLevelClient client = new RestHighLevelClient(builder);

    }
}*/
