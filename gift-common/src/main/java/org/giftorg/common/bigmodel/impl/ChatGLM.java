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

package org.giftorg.common.bigmodel.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.config.Config;
import org.giftorg.common.elasticsearch.Elasticsearch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ChatGLM implements BigModel {
    private static final String API_URL = Config.chatGLMConfig.getHostUrl();
    private static final String API_KEY = Config.chatGLMConfig.getApiKey();

    @Override
    public String chat(List<Message> messages) throws Exception {
        throw new Exception("not implemented");
    }

    /**
     * 计算文本嵌入向量
     */
    public List<Double> textEmbedding(String prompt) throws IOException {
        TextEmbeddingRequest body = new TextEmbeddingRequest(prompt);

        HttpResponse resp = HttpRequest.post(API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", genAuthorization(API_KEY, 360))
                .body(JSON.toJSONBytes(body))
                .execute();

        TextEmbeddingResponse res = JSONUtil.toBean(resp.body(), TextEmbeddingResponse.class);

        if (!res.success) {
            log.error(" response error, response.body: {}", resp.body());
            throw new RuntimeException("chat response error, response.body: " + resp.body());
        }
        return res.data.embedding;
    }

    public static class TextEmbeddingRequest {
        public String prompt;

        public TextEmbeddingRequest(String prompt) {
            this.prompt = prompt;
        }
    }

    /**
     * 生成鉴权token
     */
    public static String genAuthorization(String apiKey, long expSeconds) {
        String[] parts = apiKey.split("\\.");
        if (parts.length != 2) {
            throw new RuntimeException("chatglm invalid api key");
        }

        String id = parts[0];
        String secret = parts[1];
        Map<String, Object> payload = new HashMap<>();

        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + (expSeconds * 1000L);
        payload.put("api_key", id);
        payload.put("exp", expirationTimeMillis);
        payload.put("timestamp", currentTimeMillis);

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("alg", "HS256");
        headerMap.put("sign_type", "SIGN");

        return JWTUtil.createToken(headerMap, payload, secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 嵌入向量接口响应
     */
    @Data
    public static class TextEmbeddingResponse {
        public Integer code;
        public String msg;
        public Boolean success;
        public TextEmbeddingResponseData data;
    }

    @Data
    public static class TextEmbeddingResponseData {
        public List<Double> embedding;
    }
}
