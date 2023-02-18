package fr.bramsou.netty.messaging.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public interface TaskHandler {

    ThreadPoolExecutor THREAD_POOL_EXECUTOR = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    default void executeTask(Runnable runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    default void closeTasks() {
        THREAD_POOL_EXECUTOR.shutdown();
    }
}
