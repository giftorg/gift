package org.giftorg.codeanalyze;

import org.giftorg.codeanalyze.analyzer.CodeAnalyzer;
import org.giftorg.codeanalyze.analyzer.impl.JavaCodeAnalyzer;
import org.giftorg.codeanalyze.code.Function;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGLM;
import org.giftorg.common.elasticsearch.Elasticsearch;

import java.io.IOException;
import java.util.List;

public class Application {
    public static void main(String[] args) throws Exception {
//        CodeAnalyzer analyzer = new JavaCodeAnalyzer();
//        List<Function> funcList = analyzer.getFuncList(ClassLoader.getSystemResource("XingHuo.java").getPath());
//        funcList.forEach(f -> {
//            try {
//                System.out.println(f);
//                f.insertElasticsearch();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });

        BigModel model = new ChatGLM();

        List<Double> embedding = model.textEmbedding("怎么做文本向量化");

        List<Function> functions = Elasticsearch.retrieval("gift_function", "embedding", embedding, Function.class);
        functions.forEach(System.out::println);

        Elasticsearch.close();
    }
}
