package com.freeloop.student.v4.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;

public class FutureControlDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executor.submit(() -> {
                System.out.println("耗时任务开始");
                try {
                    Thread.sleep(3000);
                    //任务需要 3 秒
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("耗时任务收到中断请求");
                    return "任务被中断";
                }
                return "任务正常完成";
            });
            System.out.println("任务是否完成：" + future.isDone());
            try {
                String result = future.get(1, TimeUnit.SECONDS);
                //最多等待 1 秒
                System.out.println("任务结果：" + result);
            } catch (TimeoutException e) {
                System.out.println("等待超时，准备取消任务");

                boolean cancelled = future.cancel(true);

                System.out.println("取消是否成功：" + cancelled);
                System.out.println(
                        "任务是否已取消：" + future.isCancelled()
                );
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
