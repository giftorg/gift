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

#GitHub 仓库项目实体类

class Project:
    def __init__(self, itemName,stars,loginName,repository,description,):
        self.itemName = itemName        #项目名称
        self.stars = stars              #收藏数量
        self.loginName = loginName      #登录名
        # self.starsGrade = starsGrade    #
        self.repository = repository    #项目仓库网址
        self.description = description   #项目仓库描述

    def to_list(self):
        # 将实例的属性转换为列表
        return [self.itemName, self.stars, self.loginName,  self.repository, self.description]

    @staticmethod
    def header():
        # 返回 CSV 文件的表头
        return ['ItemName', 'Stars', 'LoginName', 'Repository', 'Description']

# 如果希望这个文件被其他模块导入时，不会自动执行下面的代码，可以使用如下形式：
# if __name__ == "__main__":
#     pass
