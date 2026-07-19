package com.freeloop.student.v4.jvm;

public class GarbageCollectionDemo {
    private static final byte[] RETAINED_DATA = new byte[1024 * 1024];
    private static volatile int checksum;

    public static void main(String[] args) {
        System.out.println(
                "长期对象大小：" + RETAINED_DATA.length
        );

        for (int i = 1; i <= 200; i++) {
            byte[] temporaryData =
                    new byte[256 * 1024];

            temporaryData[0] = (byte) i;
            checksum += temporaryData[0];

            if (i % 50 == 0) {
                System.out.println(
                        "已创建临时数组：" + i
                );
            }
        }

        System.out.println("校验值：" + checksum);
        System.out.println("程序结束");
    }
}
