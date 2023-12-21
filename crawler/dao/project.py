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
from typing import List

import pymysql.cursors

from entity.project import Project


class ProjectDao:
    def __init__(self, host: str, port: int, username: str, password: str, db: str):
        self.connection = pymysql.connect(
            host=host,
            port=port,
            user=username,
            password=password,
            db=db,
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )

    def insert(self, project: Project) -> bool:
        with self.connection.cursor() as cursor:
            sql = "INSERT INTO `projects` (`repo_id`, `name`, `full_name`, `stars`, `author`, `url`, `description`, `size`, `default_branch`) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)"
            logging.info("execute: " + sql % project.to_list())
            cursor.execute(sql, project.to_list())
            self.connection.commit()

    def get_repo_set(self) -> set[str]:
        result = set()
        with self.connection.cursor() as cursor:
            sql = 'SELECT `repo_id` FROM `projects`'
            cursor.execute(sql)
            rets = cursor.fetchall()
            for ret in rets:
                result.add(ret.get('repo_id'))
        return result

    def close(self):
        self.connection.close()

