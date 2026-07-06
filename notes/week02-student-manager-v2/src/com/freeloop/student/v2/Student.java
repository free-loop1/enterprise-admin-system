package com.freeloop.student.v2;

public class Student extends Person {
    private final String id;

    public Student(String id, String name, int age, String gender, String phone) {
        super(name, age, gender, phone);
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        this.id = id;
    }

    @Override
    public String getRole() {
        return "学生";
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + getName() + '\'' +
                ", age=" + getAge() +
                ", gender='" + getGender() + '\'' +
                ", phone='" + getPhone() + '\'' +
                '}';
    }
}
