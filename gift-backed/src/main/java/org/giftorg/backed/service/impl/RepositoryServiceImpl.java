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

package org.giftorg.backed.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giftorg.backed.dao.RepositoryESDao;
import org.giftorg.backed.entity.repository.Project;
import org.giftorg.backed.entity.repository.Repository;
import org.giftorg.backed.mapper.ProjectMapper;
import org.giftorg.backed.service.RepositoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepositoryServiceImpl  extends ServiceImpl<ProjectMapper, Project> implements RepositoryService {

    @Resource
    private ProjectMapper projectMapper;

    private static final RepositoryESDao rd = new RepositoryESDao();

    @Override
    public List<Repository> searchRepository(String query) throws Exception {
        return rd.retrieval(query);
    }
}
