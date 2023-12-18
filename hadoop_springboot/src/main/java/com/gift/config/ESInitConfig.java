package com.gift.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.giftorg.common.elasticsearch.Elasticsearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 自动配置ES客户端
 */
@Configuration
@Slf4j
public class ESInitConfig {



    //初始化连接ES 并且 注入IOC容器
    @Bean
    public RestHighLevelClient init(){
        HttpHost httpHost = HttpHost.create("http://elasticsearch:9200");
        RestClientBuilder builder = RestClient.builder(httpHost);
        return new RestHighLevelClient(builder);
    }

    //关闭
/*    @Bean
    public void close(){
        Elasticsearch.close();
    }*/
}
