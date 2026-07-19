package com.freeloop.student.v4.jvm;

/**
 * Teaching-only demo that intentionally triggers StackOverflowError.
 * Never invoke it from application code.
 */
public class StackOverflowDemo {
    private static int depth = 0;

    public static void main(String[] args) {
        try {
            recurse();
        } catch (StackOverflowError error) {
            System.out.println("发生栈溢出");
            System.out.println("递归深度约为：" + depth);
        }
    }

    private static void recurse() {
        depth++;
        recurse();
    }
}
