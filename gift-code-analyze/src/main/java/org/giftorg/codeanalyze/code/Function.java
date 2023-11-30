package org.giftorg.codeanalyze.code;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.shaded.org.checkerframework.checker.units.qual.C;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGLM;
import org.giftorg.common.bigmodel.impl.ChatGPT;
import org.giftorg.common.elasticsearch.Elasticsearch;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 函数实体
 */
@Data
@Slf4j
public class Function implements Serializable {
    private String name;

    private String source;

    private String description;

    private List<Double> embedding;

    private Position begin;

    private Position end;

    private String language;

    public static void initElasticsearch() throws IOException {
        Elasticsearch.EsClient().indices().create(c -> c
                .index("gift_function")
                .mappings(m -> m
                        .properties("name", p -> p.keyword(k -> k))
                        .properties("source", p -> p.keyword(k -> k))
                        .properties("description", p -> p.text(k -> k
                                .analyzer(Elasticsearch.IK_MAX_WORD_ANALYZER)
                                .searchAnalyzer(Elasticsearch.IK_SMART_ANALYZER)
                        ))
                        .properties("embedding", p -> p.denseVector(k -> k.dims(1024)))
                        .properties("begin", p -> p.object(k -> k
                                .properties("line", f -> f.integer(i -> i.index(false)))
                                .properties("column", f -> f.integer(i -> i.index(false)))
                        ))
                        .properties("end", p -> p.object(k -> k
                                .properties("line", f -> f.integer(i -> i.index(false)))
                                .properties("column", f -> f.integer(i -> i.index(false)))
                        ))
                        .properties("language", p -> p.keyword(k -> k))
                )
        );
    }

    public void insertElasticsearch() throws IOException {
        log.warn("insert function: {}", this);
        Elasticsearch.EsClient().create(c -> c
                .index("gift_function")
                .id(UUID.randomUUID().toString())
                .document(this)
        );
    }

    public static void main(String[] args) throws Exception {
        initElasticsearch();
        Elasticsearch.close();
    }

    @Override
    public String toString() {
        return "Function{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", begin=" + begin +
                ", end=" + end +
                ", language='" + language + '\'' +
                ", source=\n" + source + '\n' +
                '}';
    }

    public void analyze() {
        // 获取函数的描述信息
        BigModel gpt = new ChatGPT();
        try {
            description = gpt.chat(new ArrayList<BigModel.Message>() {{
                // 为用户输入的函数写一行不超过50字的中文注释，描述函数的作用。
                // Write a Chinese comment in one line, not exceeding 50 characters, for the user-inputted function, describing the function's purpose.
                add(new BigModel.Message("system", "Write a Chinese comment in one line, not exceeding 50 characters, for the user-inputted function, describing the function's purpose.\nInput example: \"public static void add(int a, int b) { return a + b; }\"\nOutput example: \"计算两位整数的和\""));
                add(new BigModel.Message("user", source));
            }});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BigModel glm = new ChatGLM();
        // 将函数描述向量化
        try {
            embedding = glm.textEmbedding(description);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
