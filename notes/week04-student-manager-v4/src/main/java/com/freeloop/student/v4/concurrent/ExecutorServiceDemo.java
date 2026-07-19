package com.freeloop.student.v4.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        //线程池
        for (int i = 1; i <= 4; ++i) {
            int taskId = i;

            executor.execute(
                    () -> executeTask(taskId)
            );
        }
        executor.shutdown();
        boolean completed = executor.awaitTermination(
                5,
                TimeUnit.SECONDS
        );

        if (!completed) {
            executor.shutdownNow();
        }

        System.out.println("线程池中的任务全部结束");
    }

    private static void executeTask(int taskId) {
        String threadName = Thread.currentThread().getName();
        System.out.println("任务 " + taskId + " 开始，线程：" + threadName);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(
                    "任务 " + taskId + " 被中断"
            );
            return;
        }
        System.out.println(
                "任务 " + taskId + " 结束，线程：" + threadName
        );
    }
}
