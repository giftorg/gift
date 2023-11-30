
/**
 * copyright 2023 giftorg authors
 *
 * licensed to the apache software foundation (asf) under one or more
 * contributor license agreements.  see the notice file distributed with
 * this work for additional information regarding copyright ownership.
 * the asf licenses this file to you under the apache license, version 2.0
 * (the "license"); you may not use this file except in compliance with
 * the license.  you may obtain a copy of the license at
 *
 * http://www.apache.org/licenses/license-2.0
 *
 * unless required by applicable law or agreed to in writing, software
 * distributed under the license is distributed on an "as is" basis,
 * without warranties or conditions of any kind, either express or implied.
 * see the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.giftorg.common.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.giftorg.common.config.Config;
import org.giftorg.common.utils.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HDFS 持久层操作
 */
public class HDFS {
    private static final String addr = StringUtil.trimEnd(Config.hdfsConfig.getAddr(), "/");
    private static final String reposPath = StringUtil.trimEnd(Config.hdfsConfig.getReposPath(), "/");

    public static Path hdfsPath(String path) {
        return new Path(addr + path);
    }

    public static String hdfsRepoPath(String path) {
        return hdfsPath(hdfsRepoRelPath(path)).toString();
    }

    public static String hdfsRepoRelPath(String path) {
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
     * DFS 遍历指定路径下所有文件
     */
    public static void dfs(FileSystem fs, Path path, List<String> list) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(path);

        for (FileStatus status : fileStatuses) {
            if (status.isDirectory()) {
                dfs(fs, status.getPath(), list);
            } else {
                list.add(StringUtil.trimStart(status.getPath().toString(), addr));
            }
        }
    }

    /**
     * 获取仓库列表下所有文件
     */
    public static List<String> getRepoFiles(String path) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        List<String> result = new ArrayList<>();
        dfs(fs, new Path(hdfsRepoPath(path)), result);

        fs.close();
        return result;
    }
}
