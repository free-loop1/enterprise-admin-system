package com.freeloop.student.v4.jvm;

public class StackFrameDemo {
    public static void main(String[] args) {
        System.out.println("main 开始");

        recurse(1);

        System.out.println("main 结束");
    }
    private static void recurse(int level) {
        System.out.println("进入第 " + level + " 层");

        if (level < 3) {
            recurse(level + 1);
        }

        System.out.println("离开第 " + level + " 层");
    }
}
