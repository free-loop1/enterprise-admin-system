package com.freeloop.student.v4.jvm;

public class JvmBytecodeDemo {
    public static void main(String[] args) {
        int result = add(2, 3);
        System.out.println("结果：" + result);
    }
    private static int add(int left, int right) {
        return left + right;
    }
}
