package com.freeloop.student.v4.concurrent;

public class RaceConditionDemo {
    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        Runnable incrementTask = () -> {
            for (int i = 0; i < 1_000_000; ++i) {
                count++;
            }
        };
        Thread firstWorker = new Thread(incrementTask, "First Worker");
        Thread secondWorker = new Thread(incrementTask, "Second Worker");
        firstWorker.start();
        secondWorker.start();

        firstWorker.join();
        secondWorker.join();
        System.out.println("预期结果：2000000");
        System.out.println("实际结果：" + count);
    }
}
