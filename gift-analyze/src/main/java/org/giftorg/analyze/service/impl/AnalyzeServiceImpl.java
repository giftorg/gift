/**
 * Copyright 2023 GiftOrg Authors
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.giftorg.analyze.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.giftorg.analyze.codespliter.CodeSpliter;
import org.giftorg.analyze.codespliter.impl.JavaCodeSpliter;
import org.giftorg.analyze.dao.FunctionESDao;
import org.giftorg.analyze.dao.RepositoryESDao;
import org.giftorg.analyze.entity.Function;
import org.giftorg.analyze.entity.Repository;
import org.giftorg.analyze.service.AnalyzeService;
import org.giftorg.common.hdfs.HDFS;
import scala.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class AnalyzeServiceImpl implements AnalyzeService {
    private static final FunctionESDao fd = new FunctionESDao();
    private static final RepositoryESDao rd = new RepositoryESDao();
    private final JavaSparkContext sc;

    public AnalyzeServiceImpl(SparkContext sc) {
        this.sc = new JavaSparkContext(sc);
    }

    /**
     * 分析程序启动入口
     */
    public void run(Repository repository) throws IOException {
        analyzeRepository(repository);
    }

    /**
     * 项目仓库分析
     */
    private void analyzeRepository(Repository repository) throws IOException {
        // 获取仓库中所有文件
        List<String> files = HDFS.getRepoFiles(repository.getHdfsPath());

        String readme = null;
        for (String file : files) {
            if (file.endsWith("README.md")) {
                if (readme == null) {
                    readme = file;
                }
            } else {
                // 其它文件进行代码分析
                analyzeCode(repository.getId(), file);
            }
        }

        // 项目文档分析
        handleDoc(repository, readme);
    }

    /**
     * 代码文件分析
     */
    private void analyzeCode(Integer repoId, String file) {
        // TODO: 支持更多语言
        if (file.endsWith(".java")) {
            JavaPairRDD<String, String> fileRDD = sc.wholeTextFiles(file);

            JavaRDD<Function> funcRDD = fileRDD.flatMap((FlatMapFunction<Tuple2<String, String>, Function>) f -> {
                CodeSpliter analyzer = new JavaCodeSpliter();
                List<Function> functions = analyzer.splitFunctions(new ByteArrayInputStream(f._2().getBytes()));
                return functions.iterator();
            });

            JavaRDD<Function> analyzeFuncRDD = funcRDD.map(function -> {
                boolean isOfHighValue = function.analyze();
                function.setRepoId(repoId);
                function.setFilePath(file);
                return isOfHighValue ? function : null;
            });

            for (Function function : analyzeFuncRDD.collect()) {
                if (function == null) return;
                try {
                    fd.insert(function);
                    log.info("analyze function success: {}", function);
                } catch (Exception e) {
                    log.error("analyze function failed: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 仓库文档处理
     */
    private void handleDoc(Repository repository, String doc) {
        JavaPairRDD<String, String> fileRDD = sc.wholeTextFiles(doc);

        JavaRDD<Repository> repoRDD = fileRDD.map(docContent -> {
            Repository repo = repository.clone();
            repo.setReadme(docContent._2());
            repo.translation();
            repo.tagging();
            return repo;
        });

        try {
            for (Repository repo : repoRDD.collect()) {
                if (repo == null) return;
                rd.insert(repo);
                log.info("analyze repository success: {}", repo);
            }
        } catch (Exception e) {
            log.error("analyze repository failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
