package org.giftorg.codeanalyze.code;

import lombok.Data;
import org.giftorg.common.elasticsearch.Elasticsearch;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 函数实体
 */
@Data
public class Function {
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
                ", source='" + source + '\'' +
                ", description='" + description + '\'' +
                ", embedding=" + embedding +
                ", begin=" + begin +
                ", end=" + end +
                ", language='" + language + '\'' +
                '}';
    }
}
