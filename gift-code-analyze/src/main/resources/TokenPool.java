package org.giftorg.common.tokenpool;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Getter
public class TokenPool extends Thread {
    // token 列表
    private final List<String> tokens;

    // 可用 token 队列
    private final BlockingQueue<String> tokenQueue;

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
    private ScheduledExecutorService tokenScheduler;

    public TokenPool(List<String> tokens, Integer cycle, Integer frequency) {
        this.tokens = tokens;
        this.cycle = cycle;
        this.frequency = frequency;
        this.tokenQueue = new LinkedBlockingQueue<>();
        this.apiTaskQueue = new LinkedBlockingQueue<>();
        this.start();
    }

    public TokenPool(List<String> tokens, Integer cycle, Integer frequency, Integer maxThread) {
        this.tokens = tokens;
        this.cycle = cycle;
        this.frequency = frequency;
        this.tokenQueue = new LinkedBlockingQueue<>();
        this.apiTaskQueue = new LinkedBlockingQueue<>();
        this.maxThread = maxThread;
        this.start();
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
        // 定时重置 token 队列
        tokenScheduler = Executors.newScheduledThreadPool(1);
        tokenScheduler.scheduleAtFixedRate(() -> {
            while (!tokenQueue.isEmpty()) {
                tokenQueue.poll();
            }
            tokens.forEach(token -> {
                for (int i = 0; i < frequency; i++)
                    tokenQueue.add(token);
            });
        }, 0, cycle, TimeUnit.SECONDS);

        // 初始化一个线程池
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
            String token;
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
                    apiTask.run(token);
                } catch (Exception e) {
                    log.error("api task error: {}", e.toString());
                }
            });
        }
    }


    public void close() {
        active = false;
        tokenScheduler.shutdown();
        executorService.shutdown();
        super.interrupt();
    }
}
