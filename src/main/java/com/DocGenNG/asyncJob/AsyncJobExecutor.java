package com.DocGenNG.asyncJob;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncJobExecutor {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 5000; // 5 seconds

    private ThreadPoolExecutor executor;

    public AsyncJobExecutor() {
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    public void executeAsyncJob(Runnable job) {
        executor.execute(job);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
