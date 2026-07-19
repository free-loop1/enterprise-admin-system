package com.freeloop.student.v4.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounterDemo {

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    //它适合多个线程共同修改一个整数，例如计数器、序号生成、单个整数的加减、单个变量的原子更新

    public static void main(String[] args) throws InterruptedException {
        Runnable incrementTask = () -> {
            for (int i = 0; i < 1_000_000; ++i) {
                COUNT.incrementAndGet();// 加 1，然后返回新值
            }
        };
        Thread firstWorker = new Thread(incrementTask, "First Worker");
        Thread secondWorker = new Thread(incrementTask, "Second Worker");
        firstWorker.start();
        secondWorker.start();
        firstWorker.join();
        secondWorker.join();
        System.out.println("预期结果：2000000");
        System.out.println("实际结果：" + COUNT.get());
    }
}
