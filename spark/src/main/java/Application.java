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

import lombok.extern.slf4j.Slf4j;
import org.giftorg.spark.entity.Project;
import org.giftorg.spark.service.ProjectService;
import org.giftorg.spark.service.impl.ProjectServiceImpl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Application {
    private static final ProjectService projectService = new ProjectServiceImpl();

    public static void main(String[] args) {
        List<Project> projects = projectService.getProjectList();

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        projects.subList(40, 50).forEach(project -> {
            executorService.submit(new AThread(project));
        });
    }


}

@Slf4j
class AThread extends Thread {
    private static final ProjectService projectService = new ProjectServiceImpl();

    private final Project project;

    public AThread(Project project) {
        this.project = project;
    }


    /**
     * run 处理单个项目
     */
    @Override
    public void run() {
        String remote = project.getRepository();
        if (!projectService.cloneAndPutProject(remote)) {
            // TODO 重试
            log.error("clone and put project {} failed", remote);
        }
    }
}