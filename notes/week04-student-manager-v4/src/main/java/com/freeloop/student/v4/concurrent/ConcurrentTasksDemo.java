package com.freeloop.student.v4.concurrent;

public class ConcurrentTasksDemo {
    // This is a scheduling demo. It does not make the student repositories thread-safe.
    public static void main(String[] args) throws InterruptedException {
        Thread importWorker = new Thread(
                () -> executeTask("导入学生"),
                "import-worker"
        );
        Thread statisticsWorker = new Thread(
                () -> executeTask("统计学生"),
                "statistics-worker"
        );
        importWorker.start();
        statisticsWorker.start();

        importWorker.join();
        statisticsWorker.join();
        System.out.println("全部任务完成");
    }

    private static void executeTask(String taskName) {
        for (int i = 1; i <= 5; ++i) {
            System.out.println(
                    taskName + "：第" + i + "步，线程：" + Thread.currentThread().getName()
            );
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
