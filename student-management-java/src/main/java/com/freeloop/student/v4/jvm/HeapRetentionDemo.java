package com.freeloop.student.v4.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Teaching-only demo that intentionally exhausts the heap.
 * Run it in a separate JVM with a small, explicit -Xmx value.
 */
public class HeapRetentionDemo {
    public static void main(String[] args) {
        List<byte[]> retainedData = new ArrayList<>();
        int count = 0;
        try {
            while (true) {
                retainedData.add(
                        new byte[256 * 1024]
                );

                count++;

                if (count % 10 == 0) {
                    System.out.println(
                            "当前保留数组数量：" + count
                    );
                }
            }
        } catch (OutOfMemoryError error) {
            System.out.println("发生 Java 堆内存溢出");
            System.out.println(
                    "发生溢出前保留数组数量约为：" + count
            );
        }
    }
}
