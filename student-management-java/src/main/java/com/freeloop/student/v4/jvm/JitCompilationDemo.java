package com.freeloop.student.v4.jvm;

public class JitCompilationDemo {
    private static volatile long resultSink;
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        //正式 Java 基准测试通常使用 JMH
        for (int round = 1; round <= 20; round++) {
            long total = 0;

            for (int value = 0; value < 1_000_000; value++) {
                total += calculate(value);
            }

            resultSink = total;
        }

        long elapsedTime =
                System.nanoTime() - startTime;

        System.out.println("最终结果：" + resultSink);
        System.out.println(
                "耗时毫秒：" + elapsedTime / 1_000_000.0
        );
    }

    private static int calculate(int value) {
        return value * 31 + 7;
    }
}
