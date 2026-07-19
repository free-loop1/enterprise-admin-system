package com.freeloop.student.v4.concurrent;

public class SynchronizedCounterDemo {
    private static int count = 0;

    private static synchronized void increment() {
        count++;
    }
    //synchronized 是 Java 关键字，用于实现线程之间的互斥和同步；它使用的是对象关联的监视器锁，也叫 monitor

    public static void main(String[] args) throws InterruptedException {
        Runnable incrementTask = () -> {
            for (int i = 0; i < 1_000_000; ++i) {
                increment();
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
