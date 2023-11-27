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

package org.giftorg.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class CmdUtil {
    /**
     * 执行 cmd 命令
     *
     * @return true: 执行成功; false: 执行失败
     */
    public static Boolean exec(String name, String... arg) {
        String cmd = name + " " + String.join(" ", arg);
        try {
            Process process = Runtime.getRuntime().exec(cmd);

            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                log.info("[exec stdout] " + line);
            }
            while ((line = stderrReader.readLine()) != null) {
                log.info("[exec stderr] " + line);
            }
            stdoutReader.close();
            stderrReader.close();

            return process.waitFor() == 0;
        } catch (Exception e) {
            log.error("exec cmd error: {}", cmd, e);
            return false;
        }
    }
}

