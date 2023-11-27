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

package org.giftorg.scheduler.dao;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.giftorg.common.config.Config;
import org.giftorg.common.utils.StringUtil;

import java.io.IOException;

/**
 * HDFS 持久层操作
 */
public class HDFSDao {
    private static final String addr = StringUtil.trimEnd(Config.hdfsConfig.getAddr(), "/");
    private static final String reposPath = StringUtil.trimEnd(Config.hdfsConfig.getReposPath(), "/");

    private static Path hdfsPath(String path) {
        return new Path(addr + path);
    }

    public static String hdfsRepoPath(String path) {
        return reposPath + path;
    }

    /**
     * 上传文件
     */
    public static void put(String src, String dst) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        fs.copyFromLocalFile(new Path(src), hdfsPath(dst));
        fs.close();
    }

    /**
     * 上传文件到仓库列表
     */
    public static void putRepo(String src) throws IOException {
        put(src, Config.hdfsConfig.getReposPath());
    }
}
