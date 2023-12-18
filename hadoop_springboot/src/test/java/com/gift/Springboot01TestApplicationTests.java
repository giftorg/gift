package com.gift;

//import org.giftorg.analyze.dao.FunctionESDao;
import com.alibaba.fastjson.JSON;
import com.gift.domain.Project;
import com.gift.domain.Vo.FunctionVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@Slf4j
class Springboot01TestApplicationTests {

/*    @Test
    void Test1(){

    }*/

    @Autowired
    private RestHighLevelClient client;

//    @Autowired  低级别客户端 老版本ES
//    private ElasticsearchRestTemplate restTemplate;

    @BeforeEach
    void setUp(){
        HttpHost httpHost = HttpHost.create("http://elasticsearch:9200");
        RestClientBuilder builder = RestClient.builder(httpHost);
        this.client = new RestHighLevelClient(builder);
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }


    /** 索引API client.indices()
     * 创建索引
     * @throws IOException
     */
    @Test
    void esTest() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("lka_test");
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * 类似与数据库建表
     * text:可以被分词
     * @throws IOException
     */
    @Test
    void testCreatIndexByIK() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("books");
        //设置请求中的参数
        String json = "{\n" +
                "    \"mappings\":{\n" +
                "        \"properties\":{\n" +
                "            \"id\":{\n" +
                "                \"type\":\"keyword\"\n" +
                "            },\n" +
                "            \"name\":{\n" +
                "                \"type\":\"text\",\n" +
                "                \"analyzer\":\"ik_max_word\",\n" +
                "                \"copy_to\":\"all\"\n" +
                "            },\n" +
                "            \"description\":{\n" +
                "                \"type\":\"text\",\n" +
                "                \"analyzer\":\"ik_max_word\",\n" +
                "                \"copy_to\":\"all\"\n" +
                "            },\n" +
                "             \"all\":{\n" +
                "                \"type\":\"text\",\n" +
                "                \"analyzer\":\"ik_max_word\"\n" +
                "            }\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "}";
        request.source(json, XContentType.JSON);
        client.indices().create(request,RequestOptions.DEFAULT);
    }

    /**
     * 插入一条文档 数据
     * @throws IOException
     */
/*    @Test
    void testCreatDoc() throws IOException {
        Book book = bookDao.selectById(1);

        IndexRequest request = new IndexRequest("books").id(book.getId().toString());
        String json = JSON.toJSONString(book);  //转换为Jeson对象
        request.source(json, XContentType.JSON);
        client.index(request,RequestOptions.DEFAULT);
    }*/

    /**
     * 批量插入文档
     * @throws IOException
     */
    /*@Test
    void testCreatAllDoc() throws IOException {
        List<Book> books = bookDao.selectList(null);
        BulkRequest bulkrequest = new BulkRequest();    //批处理请求

        for (Book book : books){
            IndexRequest request = new IndexRequest("books").id(book.getId().toString());
            String json = JSON.toJSONString(book);  //转换为Jeson对象
            request.source(json, XContentType.JSON);

            bulkrequest.add(request);
        }
        client.bulk(bulkrequest, RequestOptions.DEFAULT);
    }*/

    @Test
        //简单查询
    void testGet() throws IOException {

        GetRequest request = new GetRequest("gift_function","1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String json = response.getSourceAsString();
        System.out.println(json);
    }

    //按条件查询原始仓库数据 new
    @Test
    void testProjectSearch() throws IOException {

        SearchRequest request = new SearchRequest("gift_repository");
        //设置条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
//        builder.query(QueryBuilders.termQuery("name","SpringBoot-Dubbo-Docker-Jenkins"));
        //多条件文本查询 在四个字段中查询
        builder.query(QueryBuilders.multiMatchQuery("SpringBoot-Dubbo-Docker-Jenkins", "name", "description", "readme", "readmeCn", "tags"));
        request.source(builder);

        //拿到数据后怎么用
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();   //得到命中的所有数据

        for (SearchHit hit: hits) {             //循环遍历每一个数据
            String source = hit.getSourceAsString();

            if (source == null){
                log.info("查询为空");
            }

            Project repository  = JSON.parseObject(source, Project.class);//  将Json转换为指定对象输出
            System.out.println(repository);
        }
    }

    //按条件查询解析后的仓库代码数据
    @Test
    void testSearch() throws IOException {

        SearchRequest request = new SearchRequest("gift_function");
        //设置条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("name","PageRequest"));
        request.source(builder);

        //拿到数据后怎么用
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();   //得到命中的所有数据

        for (SearchHit hit: hits) {             //循环遍历每一个数据
            String source = hit.getSourceAsString();    //字符串数据

            FunctionVo functionVo  = JSON.parseObject(source, FunctionVo.class);//  将Json转换为指定对象输出
            System.out.println(functionVo);
        }
    }

/*    @Autowired
    private ElasticsearchClient client;

    @Test
    void contextLoads() throws IOException {
        CreateIndexResponse indexResponse = client.indices().create(c -> c.index("user"));
    }*/

/*    @Autowired
    private FunctionESDao functionESDao;

    @Test
    void testEs(){
        try {
            functionESDao.retrieval("Go语言语法");
        } catch (Exception exception) {

        }
    }*/

}
