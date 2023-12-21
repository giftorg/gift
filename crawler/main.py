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

import csv
import time
from queue import Queue
from typing import List

import requests

from dao.project import ProjectDao
from entity.project import Project
from languages.languages import Languages
from mq.mq import *

pd = ProjectDao('mysql', 23306, 'root', 'root', 'gift')
mq = CrawlerMQ('kafka:9092')
repo_set: set[str] = pd.get_repo_set()


def getGithubRepoList(lang: str, page: int, count: int) -> List[Project]:
    """
    获取 Github 中指定编程语言第 page 页的仓库列表
    """

    # TODO: 实现更好的数据采集策略
    desc = 'spring'
    url = f'https://api.github.com/search/repositories?q={desc}+in:name+in:readme+in:description+language:{lang}&sort=stars&page={page}&per_page={count}'
    logging.info(f"正在获取：{url} ...")
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
            id=int(repo.get('id')),
            item_name=repo.get('name'),
            stars=int(repo.get('watchers_count')),
            login_name=repo.get('owner').get('login'),
            repository=repo.get('html_url'),
            description=repo.get('description'),
            size=int(repo.get('size')),
            full_name=repo.get('full_name'),
            default_branch=repo.get('default_branch')
        )
        logging.info(f'project-{project}')
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


class Task:
    max_retry_count = 5

    def __init__(self, lang: Languages, page: int, retry_count: int):
        self.lang = lang
        self.page = page
        self.retry_count = retry_count

    def getTask(self) -> (Languages, int, bool):
        self.retry_count += 1
        return self.lang, self.page, self.retry_count < self.max_retry_count

    def __str__(self):
        return '{' + f'lang: {lang}, page: {page}' + '}'


# 初始化任务队列
def init_task_queue() -> Queue:
    # 任务队列
    task_queue = Queue()
    # 每种语言最大的页数 10个页面 每页100个仓库
    lang_max_page = 10

    for lang in Languages:
        for p in range(1, lang_max_page + 1):
            task_queue.put(Task(lang.value, p, 0))

    return task_queue


# 持久化项目列表
def save_project_list(projects):
    for p in projects:
        if p.id not in repo_set:
            try:
                pd.insert(p)
                mq.publish(CRAWLER_TOPIC, CrawlerTask(p.id, DEFAULT_RETRY_COUNT))
            except Exception as e:
                logging.error(f'插入项目 {p} 失败：{e}')
            time.sleep(0.2)


if __name__ == '__main__':
    task_queue = init_task_queue()

    # 遍历任务队列
    while not task_queue.empty():
        task = task_queue.get()
        try:
            lang, page, ok = task.getTask()
            if not ok:
                logging.error(f'task-{task}：超出最大重试次数')
            projects = getGithubRepoList(lang, page, 100)
            save_project_list(projects)
        except Exception as e:
            # 执行失败，放到队列重试
            task_queue.put(task)
            logging.error(e)
        time.sleep(1)

    pd.close()

