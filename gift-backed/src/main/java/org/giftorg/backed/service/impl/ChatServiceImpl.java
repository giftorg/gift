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

import org.giftorg.backed.service.ChatService;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGPT;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ChatServiceImpl implements ChatService {

    private static final BigModel gpt = new ChatGPT();

    @Override
    public String chat(String question) throws Exception {
        ArrayList<BigModel.Message> messages = new ArrayList<>();
        messages.add(new BigModel.Message("user", question));

        return gpt.chat(messages);
    }

}
