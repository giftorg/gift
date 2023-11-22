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

package org.giftorg.spark;

public class TestProject {
    // 下面这个是打标签的测试代码：
    public static void main(String[] args) throws Exception {
       // String t2 = "My teacher, Miss Wang, helped me a lot in my middle school life. She is a kind easygoing woman. I must thank her for making a confident girl.　　I used to be a shy and unconfident girl. Mrs. Wang noticed me. She took good care of me and encouraged me to join the school speech contest. Of course, I failed. But Mrs. Wang cheered me up and said every man is the architect of his own future.　　From then on, I practiced every day. It goes without saying “No pain, no gain.” I won the contest in the second term. In my opinion, teachers are the same as gardeners and they volunteer today and gain tomorrow. Not only can they teach knowledge but also they can teach students how to be a successful man.　　Thank you, Mrs. Wang. You make a duck become a beautiful swan. I want to be a teacher that as same as you in the future.";
        String t1 = "老洋之家`项目是一个（个人博客系统），包括前台博客页面及后台管理系统，基于SpringBoot+MyBatis实现，采用Tomcat容器部署。前台博客页面包含：首页、博客文章页面、博客分类、归档页面、关于我页面、搜索页面、登录页面。 后台管理系统包含：博客管理、分类管理、标签管理、用户管理 ";
        Project p = new Project(t1, "1");   //创建一个project示例
        p.setTranslation(p.translateToProject(t1)); //统一先调用大数据翻译方法进行翻译，若是英文就翻译输出，是中文就原样输出
        p.setTags(p.taggingToProject(p.getTranslation()));  //调用大数据打标签方法来设置标签
        System.out.println(p.getTags());    //输出该project的标记的标签
    }



    // 下面这个是翻译的测试代码：
//    public static void main(String[] args) throws Exception {
////        String t1 = "My teacher, Miss Wang, helped me a lot in my middle school life. She is a kind easygoing woman. I must thank her for making a confident girl.　　I used to be a shy and unconfident girl. Mrs. Wang noticed me. She took good care of me and encouraged me to join the school speech contest. Of course, I failed. But Mrs. Wang cheered me up and said every man is the architect of his own future.　　From then on, I practiced every day. It goes without saying “No pain, no gain.” I won the contest in the second term. In my opinion, teachers are the same as gardeners and they volunteer today and gain tomorrow. Not only can they teach knowledge but also they can teach students how to be a successful man.　　Thank you, Mrs. Wang. You make a duck become a beautiful swan. I want to be a teacher that as same as you in the future.";
//        String t1 = "老洋之家`项目是一个（个人博客系统），包括前台博客页面及后台管理系统，基于SpringBoot+MyBatis实现，采用Tomcat容器部署。前台博客页面包含：首页、博客文章页面、博客分类、归档页面、关于我页面、搜索页面、登录页面。 后台管理系统包含：博客管理、分类管理、标签管理、用户管理 ";
//        Project p = new Project(t1, "1"); //创建一个project示例
//        p.setTranslation(p.translateToProject(t1));   //调用大数据翻译方法进行翻译，若是英文就翻译输出，是中文就原样输出
//        System.out.println(p.getTranslation());   //输出该project的翻译
//    }
}
