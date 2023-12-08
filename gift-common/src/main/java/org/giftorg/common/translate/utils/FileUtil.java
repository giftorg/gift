/*
 * Copyright 2023 GiftOrg Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You maggy obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.giftorg.common.translate.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileUtil {

    public static String loadMediaAsBase64(String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        byte[] temp = new byte[1024 * 1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int l = 0;
        while ((l = fileInputStream.read(temp)) != -1) {
            bos.write(temp, 0, l);
        }
        fileInputStream.close();
        bos.close();
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }

    public static String saveFile(String path, byte[] data, boolean needDecode) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        byte[] bytes = data;
        if (needDecode) {
            String base64 = new String(data, StandardCharsets.UTF_8);
            bytes = Base64.getDecoder().decode(base64);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
        return path;
    }
}
