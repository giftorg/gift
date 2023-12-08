/**
 * Copyright 2023 GiftOrg Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.giftorg.analyze

import org.apache.spark.SparkContext
import org.giftorg.analyze.codespliter.impl.JavaCodeSpliter
import org.giftorg.analyze.dao.{FunctionESDao, RepositoryESDao}
import org.giftorg.analyze.entity.Repository
import org.giftorg.common.hdfs.HDFS
import org.slf4j.LoggerFactory

import java.io.ByteArrayInputStream
import scala.collection.JavaConverters._

class Analyzer(val sc: SparkContext) extends Serializable {
  private val fd = new FunctionESDao()
  private val rd = new RepositoryESDao()

  private val log = LoggerFactory.getLogger(this.getClass)

  /**
   * 分析程序启动入口
   */
  def run(repository: Repository): Unit = {
    analyzeRepository(repository)
  }

  /**
   * 项目仓库分析
   */
  private def analyzeRepository(repository: Repository): Unit = {
    // 获取仓库中所有文件
    val files = HDFS.getRepoFiles(repository.hdfsPath).asScala.toArray
    var docs = List.empty[(String, String)]

    files.foreach(file => {
      if (file.endsWith("README.md")) {
        // 如果文件为README文档，则进行文档分析
        handleDoc(repository, file)
      } else {
        // 其它文件进行代码分析
         analyzeCode(repository.id, file)
      }
    })
  }

  /**
   * 代码文件分析
   */
  private def analyzeCode(repoId: Integer, file: String): Unit = {
    if (file.endsWith(".java")) {
      val fileRDD = sc.wholeTextFiles(file)
      fileRDD.flatMap(file => {
        val analyzer = new JavaCodeSpliter()
        val functions = analyzer.splitFunctions(new ByteArrayInputStream(file._2.getBytes()))
        functions.asScala.toList
      }).map(function => {
        function.analyze()
        function.setRepoId(repoId)
        function.setFilePath(file)
        function
      }).collect().foreach(function => {
        try {
          fd.insert(function)
          log.info("analyze function success: {}", function)
        } catch {
          case e: Exception => println("analyze function failed: " + e.getMessage)
        }
      })
    }
  }

  /**
   * 仓库文档处理
   */
  private def handleDoc(repository: Repository, doc: String): Unit = {
    sc.wholeTextFiles(doc).map(doc => {
      val repo = repository.clone()
      repo.readme = doc._2
      repo.translation()
      repo.tagging()
      repo
    }).collect().foreach(repo => {
      if (repo == null) return
      try {
        rd.insert(repo)
        log.info("analyze repository success: {}", repo)
      } catch {
        case e:
          Exception => println("analyze repository failed: " + e.getMessage)
          e.printStackTrace()
      }
    })
  }
}
