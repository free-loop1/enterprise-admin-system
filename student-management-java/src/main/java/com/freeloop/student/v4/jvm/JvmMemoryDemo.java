package com.freeloop.student.v4.jvm;

import com.freeloop.student.v4.Gender;
import com.freeloop.student.v4.Student;

public class JvmMemoryDemo {
    public static void main(String[] args) {
        int age = 18;
        Student student = new Student(
                "S001",
                "张三",
                age,
                Gender.MAN,
                "13800000000"
        );
        printStudent(student);
    }

    private static void printStudent(Student student) {
        String message = student.getName() + "，年龄：" + student.getAge();
        System.out.println(message);
    }
}
