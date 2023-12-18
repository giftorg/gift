package com.gift.service.impl;


import com.alibaba.fastjson.JSON;
import com.gift.domain.Project;
import com.gift.domain.Repository;
import com.gift.service.RepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.giftorg.common.elasticsearch.Elasticsearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RepositoryServiceImpl implements RepositoryService {

    @Autowired
    private RestHighLevelClient client;

//    public static void main(String[] args) {
//        test1 test1 = new test1();
//        test1.testIndex("");
//    }

    @Override
    public void testIndex(String index){
//        System.out.println(client);

        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            client.indices().create(request, RequestOptions.DEFAULT);
            //
            Elasticsearch.close();
        } catch (IOException e) {
            log.info("插入出错,{}",e);
        }

    }


    @Override
    public List<Repository> SearchRepository(String text){
        SearchRequest request = new SearchRequest("gift_repository");
        //设置条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
//        builder.query(QueryBuilders.termQuery("name","SpringBoot-Dubbo-Docker-Jenkins"));
        //多条件文本查询 在四个字段中查询
        builder.query(QueryBuilders.multiMatchQuery(text, "name", "description", "readme", "readmeCn", "tags"));
        request.source(builder);

        //拿到数据后怎么用
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("查询ES仓库出错，{}",e);
        }
        SearchHits hits = response.getHits();   //得到命中的所有数据

        //新建返回仓库列表
        ArrayList<Repository> repositories = new ArrayList<>();

        for (SearchHit hit: hits) {             //循环遍历每一个数据
            String source = hit.getSourceAsString();

            if (source == null){
                log.info("查询为空");
            }

            Repository repository  = JSON.parseObject(source,Repository.class);//  将Json转换为指定对象输出
            System.out.println(repository);

            repositories.add(repository);
        }
        //
        Elasticsearch.close();

        return repositories;
    }


}
