package org.giftorg.common.tokenpool;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

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

    public static TokenPool getTokenPool(List<String> tokens, Integer cycle, Integer frequency) {
        if (tokenPool == null) {
            tokenPool = new TokenPool(tokens, cycle, frequency);
        }
        return tokenPool;
    }

    public static TokenPool getTokenPool(List<String> tokens, Integer cycle, Integer frequency, Integer maxThread) {
        if (tokenPool == null) {
            tokenPool = new TokenPool(tokens, cycle, frequency, maxThread);
        }
        return tokenPool;
    }

    public void addTask(APITask apiTask) {
        apiTaskQueue.add(apiTask);
    }

    public APITaskResult runTask(APITask apiTask) {
        APITasker apiTasker = new APITasker(apiTask);
        apiTaskQueue.add(apiTasker);
        try {
            return apiTasker.getResult();
        } catch (InterruptedException e) {
            return new APITaskResult(e);
        }
    }

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

    public void close() {
        active = false;
        tokenTimer.cancel();
        executorService.shutdown();
        super.interrupt();
    }

    public static void closeDefaultTokenPool() {
        if (tokenPool != null) {
            tokenPool.close();
        }
    }
}
