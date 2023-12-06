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

class Project:
    """
    GitHub 仓库项目实体类
    """

    def __init__(self, id: int, item_name: str, stars: int, login_name: str, repository: str, description: str,
                 size: int, full_name: str, default_branch: str):
        self.id = id  # 项目ID
        self.item_name = item_name  # 项目名称
        self.stars = stars  # 收藏数量
        self.login_name = login_name  # 登录名
        self.repository = repository  # 项目仓库网址
        self.description = description  # 项目仓库描述
        self.size = size  # 项目大小
        self.full_name = full_name  # 项目所在用户名
        self.default_branch = default_branch  # 项目的默认分支

    def to_list(self):
        """
        将实例的属性转换为列表
        """
        return (self.id, self.item_name, self.full_name, self.stars, self.login_name, self.repository, self.description, self.size,
                self.default_branch)

    @staticmethod
    def header():
        """
        返回 CSV 文件的表头
        """
        return ['Id', 'ItemName', 'FullName', 'Stars', 'LoginName', 'Repository', 'Description', 'Size',
                'DefaultBranch']

    def __str__(self):
        return '{' + f'id: {self.id}, item_name: {self.item_name}, full_name: {self.full_name}, start: {self.stars}, login_name: {self.login_name}, repository: {self.repository}, description: {self.description}, size: {self.size}, default_branch: {self.default_branch}' + '}'
