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
import time
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

    def insert(self, project: Project):
        try:
            with self.connection.cursor() as cursor:
                sql = "INSERT INTO `projects` (`item_name`, `stars`, `login_name`, `repository`, `description`) VALUES (%s, %s, %s, %s, %s)"
                logging.info("execute: " + sql % project.to_list())
                cursor.execute(sql, project.to_list())
                self.connection.commit()
        except pymysql.err.IntegrityError:
            logging.warning(f"{project} 记录重复")
            return
        except Exception as e:
            logging.error(f"{project} 插入  MySQL 失败: {e}")
            raise Exception(f"{project} 插入  MySQL 失败: {e}")

    def inserts(self, projects: List[Project]):
        try:
            for p in projects:
                self.insert(p)
                time.sleep(0.1)
        except Exception as e:
            logging.error(f"插入项目列表失败: {e}")
            raise Exception(f"插入项目列表失败: {e}")

    def close(self):
        self.connection.close()
