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

package org.giftorg.common.tokenpool;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;

@Slf4j
public class Token {
    private final String token;
    private final Queue<Long> useQueue;

    public Token(String token) {
        this.token = token;
        this.useQueue = new LinkedList<>();
    }

    public long pollLastTime() {
        Long lastTime = useQueue.poll();
        if (lastTime == null) {
            return System.currentTimeMillis();
        }
        return lastTime;
    }

    public String useToken() {
        useQueue.add(System.currentTimeMillis());
        return token;
    }
}
