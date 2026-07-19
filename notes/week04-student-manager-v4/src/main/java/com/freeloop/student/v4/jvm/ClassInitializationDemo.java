package com.freeloop.student.v4.jvm;

public class ClassInitializationDemo {
    static {
        System.out.println("外部类：静态代码块");
    }

    public static void main(String[] args) {
        System.out.println("main 开始");

        System.out.println("静态值：" + Sample.VALUE);

        new Sample();
        new Sample();

        System.out.println("main 结束");
    }

    private static class Sample {
        private static int VALUE = initializeValue();

        static {
            System.out.println("Sample：静态代码块");
        }

        private static int initializeValue() {
            System.out.println("Sample：初始化静态字段");
            return 42;
        }

        private Sample() {
            System.out.println("Sample：构造方法");
        }
    }
}
