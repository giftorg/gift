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

package org.giftorg.spark.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharsetUtilTest {

    @Test
    void testIsChinese() {
        assertTrue(CharsetUtil.isChinese("中华人民共和国万岁"));
        System.out.println(CharsetUtil.isChinese("在Java中，str.toCharArray()会被执行一次。这行代码是在for循环开始之前执行的，并且将字符串转换为字符数组。然后，for-each循环会遍历这个数组。"));
    }
}