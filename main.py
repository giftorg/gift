# Copyright 2023 GiftOrg Authors
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import requests  # 导入模块requests
import csv  #csv文件
import time
from domain.Project import Project   #导入实体类
from Enum.LanguageEnum import Languages

'''
这个文件是用来返回某种指定语言的项目仓库信息
https://api.github.com/search/repositories?q=language:python&sort=stars&page=4&per_page=100 每页100 第四页
思路，for循环遍历100×100，实现遍历100页
!!!需要定时更新
加入封装对象
'''
#统计页数
countpages = 1
# 指定 CSV 文件路径
csv_file_path = 'projects.csv'

def find100GitHub(language,pageId):
    # 执行API调用并存储响应

    url = 'https://api.github.com/search/repositories?q=language:{}&sort=stars&page={}&per_page=100'.format(language,pageId)
    # requests来执行调用
    r = requests.get(url)

    # 将API响应存储在一个变量中
    # 使用json()将这些信息转换为Python字典
    response_dict = r.json()

    # 探索有关仓库信息
    # 处理没有'items'键的情况
    # 可以抛出异常、返回默认值或进行其他处理
    if 'items' in response_dict:
        repo_dicts = response_dict['items']
    else:
        print(language,"语言目前的页数:",pageId)
        return "没有项目仓库了"


    itemlist_length = len(repo_dicts)
    if itemlist_length == 0 :
        print("Repositories returned：", len(repo_dicts))
        return
    print("\nSelected information about first repository：\n")
    count = 0
    for repo_dict in repo_dicts:
        count += 1
        print("项目", count)
        itemName = repo_dict["name"]
        stars = repo_dict["watchers_count"]
        loginName = repo_dict["owner"]["login"]
        repository = repo_dict["html_url"]
        description = repo_dict["description"]
        project = Project(itemName=itemName, stars=stars, loginName=loginName,
                repository=repository, description=description)
        print(project.to_list())
        proJect_list.append(project)
        print("\n")

# 将实例保存到 CSV 文件
def save_to_csv(file_path, projects):
    with open(file_path, mode='w', newline='',encoding='utf-8') as file:
        writer = csv.writer(file)
        # 写入 CSV 文件的表头
        writer.writerow(Project.header())
        # 写入每个实例的数据
        for project in projects:
            writer.writerow(project.to_list())


if __name__ == '__main__':
    proJect_list=[]

    flag = 0
    # 切换语言
    for language in Languages:
        print("++++++++++++%s++++++++++++" % language.value)
        if flag == 0 :
            # 换语言页数时清零
            countpages = 1
        flag = 1
        # 每个语言的列表 十个页面 一页100个
        for i in range (1,11):
            print("======================目前%s语言的第%d页=====================" % (language.value,i))
            find100GitHub(language.value,i)
            countpages += 1
        time.sleep(30)
    print("总页数为：%d页" % (countpages - 1))
    itemcounts = (countpages - 1) * 100
    print("项目总数为%d" % itemcounts)
    # 将实例列表保存到 CSV 文件
    save_to_csv(csv_file_path, proJect_list)
