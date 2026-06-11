package com.freeloop.student;

public class Student {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String phone;

    public Student(String id, String name, int age, String gender, String phone) {
        setId(id);
        setName(name);
        setAge(age);
        setGender(gender);
        setPhone(phone);
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        this.name = name;
    }

    public void setAge(int age) {
        if (age < 0 || age > 120) {
            throw new IllegalArgumentException("年龄必须在 0 到 120 之间");
        }
        this.age = age;
    }

    public void setGender(String gender) {
        if (gender == null || gender.isBlank()) {
            throw new IllegalArgumentException("性别不能为空");
        }
        if (!gender.equals("男") && !gender.equals("女")) {
            throw new IllegalArgumentException("性别只能是男或女");
        }
        this.gender = gender;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }
}
