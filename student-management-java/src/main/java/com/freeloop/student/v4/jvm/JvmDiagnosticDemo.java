package com.freeloop.student.v4.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Teaching-only process kept alive for jcmd inspection.
 * Run it separately from the student manager.
 */
public class JvmDiagnosticDemo {
    private static final List<byte[]> RETAINED_DATA =
            new ArrayList<>();
    public static void main(String[] args)
            throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            RETAINED_DATA.add(
                    new byte[512 * 1024]
            );
        }

        long processId =
                ProcessHandle.current().pid();

        System.out.println("当前进程 PID：" + processId);
        System.out.println("已保留约 5 MB 数组");
        System.out.println("等待诊断，请在另一个终端执行 jcmd");

        Thread.sleep(5 * 60 * 1000L);
    }
}
