package org.giftorg.common.tokenpool;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;

@Slf4j
class TokenPoolTest {

    @Test
    void testTokenPool() {
        TokenPool pool = new TokenPool(Arrays.asList("a", "b", "c", "d", "e"), 1, 3);

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            pool.addTask(token -> log.info("token{}: {}", finalI, token));
        }
    }

    @Test
    void testTokenPool2() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello");
            }
        }, 1000L);
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tokens.add(String.format("token-%d", i));
        }
        TokenPool pool = new TokenPool(tokens, 1, 3);

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            APITaskResult result = pool.runTask(token -> {
                log.info("token{}: {}", finalI, token);
            });
            log.info("result{}: {}", i, result);
        }

        pool.close();
    }
}