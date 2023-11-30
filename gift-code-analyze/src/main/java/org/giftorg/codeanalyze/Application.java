package org.giftorg.codeanalyze;

import org.giftorg.codeanalyze.code.Function;
import org.giftorg.common.elasticsearch.Elasticsearch;
import org.giftorg.common.tokenpool.TokenPool;

import java.io.IOException;
import java.util.List;

public class Application {
    public static void main(String[] args) throws Exception {
//        testAnalyze();
        testEmbedding();
    }

    public static void testAnalyze() throws IOException {
        CodeAnalyzeApplication app = new CodeAnalyzeApplication();
        app.run("/phodal/migration");

        Elasticsearch.close();
        TokenPool.closeDefaultTokenPool();
    }

    public static void testEmbedding() throws Exception {
        List<Function> functions = Elasticsearch.retrieval("gift_function", "description", "怎么连接数据库", "embedding", Function.class);
        functions.forEach(System.out::println);

        Elasticsearch.close();
    }
}
