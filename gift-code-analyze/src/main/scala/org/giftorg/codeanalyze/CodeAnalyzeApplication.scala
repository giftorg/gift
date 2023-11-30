package org.giftorg.codeanalyze

import org.apache.spark.{SparkConf, SparkContext}
import org.giftorg.codeanalyze.analyzer.CodeSpliter
import org.giftorg.codeanalyze.analyzer.impl.JavaCodeSpliter
import org.giftorg.common.hdfs.HDFS

import java.io.ByteArrayInputStream
import scala.collection.JavaConverters._

class CodeAnalyzeApplication {
  def run(path: String): Unit = {
    val sparConf = new SparkConf()
      .setAppName("WordCount")
      .setMaster("local[*]")

    val sc = new SparkContext(sparConf)
    analyzeRepository(sc, path)

    sc.stop()
  }

  def analyzeRepository(sc: SparkContext, path: String): Unit = {
    val files = HDFS.getRepoFiles(path).asScala.toArray

    val documents: Array[String] = Array.empty

    files.foreach(file => {
      // TODO: 处理 README 文档
//      if (file.endsWith("README.md")) {
//        documents :+ file
//      }

      analyzeCode(sc, file)
    })

  }

  def analyzeCode(sc: SparkContext, file: String): Unit = {
    val fileRDD = sc.wholeTextFiles(file)
    fileRDD.flatMap(file => {
      file._1.split("\\.").last match {
        case "java" =>
          val analyzer: CodeSpliter = new JavaCodeSpliter()
          val functions = analyzer.splitFunctions(new ByteArrayInputStream(file._2.getBytes()))
          functions.asScala.toList
        case _ => List.empty
      }
    }).map(function => {
      function.analyze()
      function
    }).collect().foreach(function => {
      function.insertElasticsearch()
      println(function, "SUCCESS")
    })
  }
}
