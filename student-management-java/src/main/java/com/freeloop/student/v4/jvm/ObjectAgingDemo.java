package com.freeloop.student.v4.jvm;

import java.util.ArrayList;
import java.util.List;

public class ObjectAgingDemo {
    private static final List<byte[]> SURVIVORS =
            new ArrayList<>();
    private static volatile int checksum;

    public static void main(String[] args) {
        createLongLivedObjects();
    }

    private static void createLongLivedObjects() {
        for (int i = 0; i < 20; i++) {
            SURVIVORS.add(
                    new byte[128 * 1024]
            );
        }
        for (int round = 1; round <= 10; round++) {
            createTemporaryObjects();

            System.out.println(
                    "第 " + round + " 轮分配完成"
            );
        }

        System.out.println(
                "长期存活数组数量：" + SURVIVORS.size()
        );
        System.out.println("校验值：" + checksum);
    }

    private static void createTemporaryObjects() {
        for (int i = 0; i < 200; i++) {
            byte[] temporaryData =
                    new byte[64 * 1024];

            temporaryData[0] = (byte) i;
            checksum += temporaryData[0];
        }
    }
}
