package com.freeloop.student.v3;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Student extends Person {
    private final String id;
    private final LocalDateTime createdAt;

    public Student(String id, String name, int age, Gender gender, String phone, LocalDateTime createdAt) {
        super(name, age, gender, phone);
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("创建时间不能为空");
        }
        this.id = id;
        this.createdAt = createdAt;
    }

    public Student(String id, String name, int age, Gender gender, String phone) {
        this(id, name, age, gender, phone, LocalDateTime.now());
    }

    @Override
    public String getRole() {
        return "学生";
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createdAt.format(formatter);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + getName() + '\'' +
                ", age=" + getAge() +
                ", gender='" + getGender() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", createdAt='" + getCreatedAtText() + '\'' +
                '}';
    }
}
