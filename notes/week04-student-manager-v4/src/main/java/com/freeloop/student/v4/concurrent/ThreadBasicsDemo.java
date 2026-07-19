package com.freeloop.student.v4.concurrent;

public class ThreadBasicsDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("主程序开始：" + Thread.currentThread().getName());
        Runnable task = () -> System.out.println("执行任务：" + Thread.currentThread().getName());
        Thread worker = new Thread(task, "student-worker");
        worker.start();//启动新线程,JVM 会启动一个新的线程，并让新线程执行 task 的 run() 方法
        worker.join();//join() 的含义是：main 线程先暂停，等待 worker 执行完成
        System.out.println("主程序结束：" + Thread.currentThread().getName());
        /*
        main：启动 worker
        main：调用 join，开始等待
        worker：执行任务
        worker：执行结束
         main：停止等待，继续运行  */
        Thread runWorker = new Thread(task, "run-worker");
        Thread startWorker = new Thread(task, "start-worker");
        System.out.println("调用 run()");
        runWorker.run();//普通方法调用，不会启动新线程
        System.out.println("调用 start()");
        startWorker.start();//由新线程执行 task.run()
        startWorker.join();// main 等待
        //同一个线程对象不能调用两次 start()
        System.out.println(
                "全部结束：" + Thread.currentThread().getName()
        );
    }
}
