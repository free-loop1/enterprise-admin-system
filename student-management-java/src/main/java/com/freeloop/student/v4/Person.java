package com.freeloop.student.v4;

public abstract class Person {
    private String name;
    private int age;
    private Gender gender;
    private String phone;

    protected Person(String name, int age, Gender gender, String phone) {
        this.name = validateName(name);
        this.age = validateAge(age);
        this.gender = validateGender(gender);
        this.phone = validatePhone(phone);
    }

    public abstract String getRole();

    public void setName(String name) {
        this.name = validateName(name);
    }

    public void setAge(int age) {
        this.age = validateAge(age);
    }

    public void setGender(Gender gender) {
        this.gender = validateGender(gender);
    }

    public void setPhone(String phone) {
        this.phone = validatePhone(phone);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        return name;
    }

    private static int validateAge(int age) {
        if (age < 0 || age > 120) {
            throw new IllegalArgumentException("年龄必须在 0 到 120 之间");
        }
        return age;
    }

    private static Gender validateGender(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("性别不能为空");
        }
        return gender;
    }

    private static String validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        return phone;
    }
}
