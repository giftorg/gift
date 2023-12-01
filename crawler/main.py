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

import logging
from queue import Queue
import time

import requests
import csv

from typing import List

from dao.project import ProjectDao
from entity.project import Project
from languages.languages import Languages


def getGithubRepoList(lang: str, page: int, count: int) -> List[Project]:
    """
    获取 Github 中指定编程语言第 page 页的仓库列表
    """

    url = f'https://api.github.com/search/repositories?q=language:{lang}&sort=stars&page={page}&per_page={count}'
    resp = requests.get(url)
    if resp.status_code != 200:
        raise Exception(f'请求仓库列表失败，状态码：{resp.status_code}')
    body: dict = resp.json()

    if 'items' not in body:
        raise Exception('获取仓库列表失败，响应结果异常')

    repos = body.get('items')
    projects: List[Project] = list()
    for repo in repos:
        project = Project(
            item_name=repo.get('name'),
            stars=repo.get('watchers_count'),
            login_name=repo.get('owner').get('login'),
            repository=repo.get('html_url'),
            description=repo.get('description'),
            size = repo.get('size'),
            id = repo.get('id'),
            full_name = repo.get('full_name'),
            default_branch = repo.get('default_branch')
        )
        print(project)
        projects.append(project)
    return projects


# 将实例保存到 CSV 文件
def save_to_csv(file_path, projects: List[Project]):
    with open(file_path, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        # 写入 CSV 文件的表头
        writer.writerow(Project.header())
        # 写入每个实例的数据
        for project in projects:
            writer.writerow(project.to_list())


# 初始化任务队列
def init_task_queue() -> Queue:
    # 任务队列
    task_queue = Queue()
    # 每种语言最大的页数 10个页面 每页100个仓库
    lang_max_page = 10

    for language in Languages:
        for p in range(1, lang_max_page + 1):
            task_queue.put((language.value, p))

    return task_queue


if __name__ == '__main__':
    pd = ProjectDao('localhost', 3306, 'root', 'root', 'test')
    # pd = ProjectDao('139.9.65.13', 23306, 'root', 'root', 'test')

    task_queue = init_task_queue()
    project_list: List[Project] = list()

    # 遍历任务队列
    while not task_queue.empty():
        task = task_queue.get()
        try:
            projects = getGithubRepoList(task[0], task[1], 100)
            pd.inserts(projects)
            project_list.extend(projects)
        except Exception as e:
            # 执行失败，放到队列重试
            task_queue.put(task)
            logging.info(e)
        time.sleep(1)

    pd.close()
    logging.info(f"项目总数为：{len(project_list)}")
