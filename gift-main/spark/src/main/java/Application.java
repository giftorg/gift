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

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.giftorg.spark.dao.HDFSDao;
import org.giftorg.spark.entity.Project;
import org.giftorg.spark.mapper.ProjectMapper;
import org.giftorg.spark.utils.GitUtil;
import org.giftorg.spark.utils.PathUtil;
import org.giftorg.spark.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class Application {
    public static void main(String[] args) {

        List<Project> projects = null;
        try {
            projects = getProjectList();
        } catch (IOException e) {
            log.error("get project list error: {}", e.getMessage());
            System.exit(1);
        }

        projects.subList(100, 110).forEach(project -> {
            String remote = project.getRepository();
            String local = FileUtil.getAbsolutePath(PathUtil.join("repos", PathUtil.base(remote)));

            // 拉取项目到本地
            Boolean ok = GitUtil.gitClone(remote, local);

            // 上传到HDFS
            if (ok) {
                log.info("git clone success. remote: {}, local: {}", remote, local);
                try {
                    String hdfsUrl = HDFSDao.hdfsRepoPath("/" + PathUtil.base(StringUtil.trimEnd(remote, "/" + PathUtil.base(remote))));
                    log.info("put repository {} to HDFS: {}", remote, hdfsUrl);
                    HDFSDao.put(local, hdfsUrl);
                } catch (Exception e) {
                    log.error("put repository {} to HDFS error: {}", remote, e.getMessage());
                }
            } else {
                log.error("git clone failed. remote: {}", remote);
            }

            // 删除本地项目
            try {
                FileUtil.del(local);
            } catch (Exception e) {
                log.warn("delete local repository {} error: {}", local, e.getMessage());
            }
        });
    }

    private static List<Project> getProjectList() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();

        ProjectMapper mapper = sqlSession.getMapper(ProjectMapper.class);
        List<Project> projectList = mapper.selectList();

        sqlSession.close();

        return projectList;
    }
}

