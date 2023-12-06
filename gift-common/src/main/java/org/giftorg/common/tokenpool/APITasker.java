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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class APITasker implements APITask {
    private final APITask task;
    private final BlockingQueue<APITaskResult> resultQueue;

    public APITasker(APITask task) {
        this.task = task;
        this.resultQueue = new LinkedBlockingQueue<>();
    }

    public void run(String token) {
        try {
            task.run(token);
            resultQueue.add(new APITaskResult());
        } catch (Exception e) {
            resultQueue.add(new APITaskResult(e));
        }
    }

    public APITaskResult getResult() throws InterruptedException {
        return resultQueue.take();
    }
}
