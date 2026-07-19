package com.freeloop.student.v4.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableFutureDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Callable<Integer> firstTask =
                    () -> calculateSum(1, 100);
            Callable<Integer> secondTask =
                    () -> calculateSum(101, 200);
            Future<Integer> firstFuture = executor.submit(firstTask);
            Future<Integer> secondFuture = executor.submit(secondTask);
            System.out.println("两个计算任务已经提交");
            int firstResult = firstFuture.get();
            int secondResult = secondFuture.get();
            System.out.println("第一个结果：" + firstResult);
            System.out.println("第二个结果：" + secondResult);

            int sum = firstResult + secondResult;
            System.out.println("最终结果：" + sum);
        } finally {
            executor.shutdown();
        }
    }

    private static int calculateSum(int start, int end) {
        System.out.println("计算 " + start + " 到 " + end
                + "，线程："
                + Thread.currentThread().getName());
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += i;
        }
        return sum;
    }
}
