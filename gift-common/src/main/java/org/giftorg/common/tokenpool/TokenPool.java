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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * API Token 池
 * 自动维护可用的 token 列表，提供给外部使用
 * 该池会在需要时提供一个全局的 token pool, 如果没有多个 token pool 的特殊要求，建议使用全局的 token pool
 * 多个 token pool 之间建议传入不同的 token list
 */
@Slf4j
@Getter
public class TokenPool extends Thread {
    // 全局 TokenPool
    private static TokenPool tokenPool = null;

    // token 列表
    private final List<Token> tokens;

    // 可用 token 队列
    private final BlockingQueue<Token> tokenQueue;

    // api 任务队列
    private final BlockingQueue<APITask> apiTaskQueue;

    // 每个 token 在每个周期的可用次数
    private final Integer frequency;

    // 周期（秒）
    private final Integer cycle;

    // 最大线程数
    private Integer maxThread = 10;

    // 线程池
    private ExecutorService executorService;

    // 程序状态
    private Boolean active = true;

    // 定时器
    private Timer tokenTimer;

    public TokenPool(List<String> tokens, Integer cycle, Integer frequency) {
        this.cycle = cycle;
        this.frequency = frequency;
        this.tokenQueue = new LinkedBlockingQueue<>();
        this.apiTaskQueue = new LinkedBlockingQueue<>();

        this.tokens = new ArrayList<>();
        for (String token : tokens) {
            Token t = new Token(token);
            this.tokens.add(t);
            for (int i = 0; i < frequency; i++) {
                tokenQueue.add(t);
            }
        }

        this.start();
    }

    public TokenPool(List<String> tokens, Integer cycle, Integer frequency, Integer maxThread) {
        this.cycle = cycle;
        this.frequency = frequency;
        this.tokenQueue = new LinkedBlockingQueue<>();
        this.apiTaskQueue = new LinkedBlockingQueue<>();
        this.maxThread = maxThread;

        this.tokens = new ArrayList<>();
        for (String token : tokens) {
            Token t = new Token(token);
            this.tokens.add(t);
            for (int i = 0; i < frequency; i++) {
                tokenQueue.add(t);
            }
        }

        this.start();
    }

    /**
     * 获取全局的 TokenPool
     */
    public static TokenPool getTokenPool(List<String> tokens, Integer cycle, Integer frequency) {
        if (tokenPool == null) {
            tokenPool = new TokenPool(tokens, cycle, frequency);
        }
        return tokenPool;
    }

    /**
     * 获取全局的 TokenPool，并指定 api token 任务执行的最大线程数
     */
    public static TokenPool getTokenPool(List<String> tokens, Integer cycle, Integer frequency, Integer maxThread) {
        if (tokenPool == null) {
            tokenPool = new TokenPool(tokens, cycle, frequency, maxThread);
        }
        return tokenPool;
    }

    /**
     * 添加 api token 任务，异步执行
     */
    public void addTask(APITask apiTask) {
        apiTaskQueue.add(apiTask);
    }

    /**
     * 添加 api token 任务，同步执行并返回执行结态
     */
    public APITaskResult runTask(APITask apiTask) {
        APITasker apiTasker = new APITasker(apiTask);
        apiTaskQueue.add(apiTasker);
        try {
            return apiTasker.getResult();
        } catch (InterruptedException e) {
            return new APITaskResult(e);
        }
    }

    /**
     * Token 池的调度程序
     */
    public void run() {
        tokenTimer = new Timer();
        executorService = Executors.newFixedThreadPool(maxThread);

        // 从队列中取出 token，执行任务
        while (active) {
            // 从任务队列中取出任务
            APITask apiTask;
            try {
                apiTask = apiTaskQueue.take();
            } catch (InterruptedException ignored) {
                continue;
            } catch (Exception e) {
                log.error("take api task error: {}", e.toString());
                continue;
            }

            // 从 token 队列中取出 token
            Token token;
            try {
                token = tokenQueue.take();
            } catch (InterruptedException ignored) {
                continue;
            } catch (Exception e) {
                log.error("take token error: {}", e.toString());
                continue;
            }

            // 执行任务
            executorService.submit(() -> {
                try {
                    apiTask.run(useToken(token));
                } catch (Exception e) {
                    e.getStackTrace();
                    log.error("api task error: {}", e.toString());
                }
            });
        }
    }

    private String useToken(Token token) {
        String t = token.useToken();
        tokenTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tokenQueue.add(token);
            }
        }, cycle * 1000 - (System.currentTimeMillis() - token.pollLastTime()));
        return t;
    }

    /**
     * 关闭 TokenPool
     */
    public void close() {
        active = false;
        tokenTimer.cancel();
        executorService.shutdown();
        super.interrupt();
    }

    /**
     * 关闭全局的 TokenPool
     */
    public static void closeDefaultTokenPool() {
        if (tokenPool != null) {
            tokenPool.close();
        }
    }
}
