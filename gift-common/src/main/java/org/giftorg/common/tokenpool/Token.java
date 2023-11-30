package org.giftorg.common.tokenpool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

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
