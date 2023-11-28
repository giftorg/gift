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

package org.giftorg.spark.bigmodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.giftorg.spark.config.Config;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class XingHou extends WebSocketListener {
    private static final String hostUrl = Config.xingHouConfig.getHostUrl();
    private static final String appid = Config.xingHouConfig.getAppid();
    private static final String apiSecret = Config.xingHouConfig.getApiSecret();
    private static final String apiKey = Config.xingHouConfig.getApiKey();

    public static final Gson gson = new Gson();

    public List<Message> messages;
    public String answer = "";
    private final CountDownLatch latch;

    public XingHou(List<Message> messages) {
        this.messages = messages;
        this.latch = new CountDownLatch(1);
    }

    /**
     * 聊天接口，接收一个消息列表，返回大模型回复的消息
     */
    public static String chat(List<Message> messages) throws Exception {
        log.info("chat request: {}", messages);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = getAuthUrl().replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();

        XingHou bigModel = new XingHou(messages);
        client.newWebSocket(request, bigModel);
        bigModel.latch.await();
        client.dispatcher().executorService().shutdown();

        log.info("chat answer: {}", bigModel.answer);
        return bigModel.answer;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        ChatThread chatThread = new ChatThread(webSocket, newRequestBody(messages));
        chatThread.start();
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        JsonParse jsonParse = gson.fromJson(text, JsonParse.class);
        if (jsonParse.header.code != 0) {
            log.error("onMessage error, code: {}, sid: {}", jsonParse.header.code, jsonParse.header.sid);
            webSocket.close(1000, "");
        }
        List<Message> texts = jsonParse.payload.choices.text;
        StringBuilder ans = new StringBuilder();
        for (Message t : texts) {
            ans.append(t.content);
        }
        log.info("onMessage: {}", ans);
        answer += ans.toString();
        if (jsonParse.header.status == 2) {
            webSocket.close(1000, "");
        }
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                log.error("onFailure code: {}", response.code());
                log.error("onFailure body: {}", response.body() != null ? response.body().string() : "");
                if (response.code() != 101) {
                    log.error("connection failed");
                }
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        latch.countDown();
        super.onClosed(webSocket, code, reason);
    }

    // 鉴权方法
    public static String getAuthUrl() throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        return httpUrl.toString();
    }

    // 创建聊天请求体
    public static JSONObject newRequestBody(List<Message> messages) {
        JSONObject request = new JSONObject();

        // header
        JSONObject header = new JSONObject();
        header.put("app_id", appid);
        header.put("uid", UUID.randomUUID().toString().substring(0, 10));

        // parameter
        JSONObject parameter = new JSONObject();
        JSONObject chat = new JSONObject();
        chat.put("domain", "generalv2");
        chat.put("temperature", 0.5);
        chat.put("max_tokens", 4096);
        parameter.put("chat", chat);

        // payload
        JSONObject payload = new JSONObject();
        JSONObject message = new JSONObject();
        JSONArray text = new JSONArray();

        // set messages
        for (Message m : messages) {
            text.add(JSON.toJSON(m));
        }
        message.put("text", text);
        payload.put("message", message);

        // request
        request.put("header", header);
        request.put("parameter", parameter);
        request.put("payload", payload);

        return request;
    }

    // 响应体结构
    static class JsonParse {
        Header header;
        Payload payload;
    }

    static class Header {
        int code;
        int status;
        String sid;
    }

    static class Payload {
        Choices choices;
    }

    static class Choices {
        List<Message> text;
    }
}

/**
 * 聊天线程
 */
@Slf4j
class ChatThread extends Thread {
    private final WebSocket webSocket;
    private final JSONObject body;

    public ChatThread(WebSocket webSocket, JSONObject body) {
        this.webSocket = webSocket;
        this.body = body;
    }

    public void run() {
        try {
            webSocket.send(body.toString());
        } catch (Exception e) {
            log.error("ChatThread error: {}", e.toString());
        }
    }
}
