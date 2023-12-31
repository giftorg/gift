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

public class CharsetUtil {
    /**
     * 判断是否为中文字符
     */
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    /**
     * 判断是否为中文字符串
     */
    public static boolean isChinese(String str) {
        double ratio = 0.5;

        int total = 0;
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (isChinese(c)) total++;
            if (total >= ratio * chars.length) return true;
        }
        return false;
    }
}
