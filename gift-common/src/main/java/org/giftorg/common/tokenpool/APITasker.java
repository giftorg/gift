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
