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

package org.giftorg.scheduler.service.impl;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.giftorg.common.hdfs.HDFS;
import org.giftorg.common.utils.GitUtil;
import org.giftorg.common.utils.PathUtil;
import org.giftorg.scheduler.entity.Project;
import org.giftorg.scheduler.mapper.ProjectMapper;
import org.giftorg.scheduler.service.ProjectService;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ProjectServiceImpl implements ProjectService {
    private static final SqlSessionFactory sqlSessionFactory;

    static {
        String resource = "mybatis-config.xml";
        try {
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void pullProject(Project project) throws Exception {
        // clone project
        String local = FileUtil.getAbsolutePath(PathUtil.join("repositories", project.getFullName()));
        GitUtil.gitClone(project.getUrl(), local);
        log.info("clone project {} success", project.getFullName());

        // put project to HDFS
        project.setHdfsPath(HDFS.hdfsRepoRelPath("/" + project.getFullName()));
        HDFS.put(local, project.getHdfsPath());
        log.info("put project {} to HDFS success", project.getFullName());

        // delete local project
        FileUtil.del(local);
        log.info("delete local project {} success", project.getFullName());
    }

    public Project getProjectByRepoId(Integer repoId) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            ProjectMapper pm = sqlSession.getMapper(ProjectMapper.class);
            return pm.selectOneByRepoId(repoId);
        } catch (Exception e) {
            log.error("get project by repoId {} failed: {}", repoId, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            ProjectMapper pm = sqlSession.getMapper(ProjectMapper.class);
            Project project = pm.selectOne(20);
            System.out.println(project);
        }
    }
}
