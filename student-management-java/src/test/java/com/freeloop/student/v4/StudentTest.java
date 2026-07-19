package com.freeloop.student.v4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StudentTest {
    @Test
    void shouldCreateStudentWithCorrectInformation() {
        Student student = new Student(
                "S001",
                "张三",
                20,
                Gender.MAN,
                "123456789"
        );

        assertEquals("S001", student.getId());
        assertEquals("张三", student.getName());
        assertEquals(20, student.getAge());
        assertEquals(Gender.MAN, student.getGender());
        assertEquals("123456789", student.getPhone());
        assertEquals("学生", student.getRole());
    }

    @Test
    void shouldRejectBlankId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Student(
                " ",
                "张三",
                20,
                Gender.MAN,
                "123456789"
        ));
        assertEquals("学号不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectBlankName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Student(
                "S001",
                " ",
                20,
                Gender.MAN,
                "123456789"
        ));
        assertEquals("姓名不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNegativeAge() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Student(
                "S001",
                "张三",
                -1,
                Gender.MAN,
                "123456789"
        ));
        assertEquals("年龄必须在 0 到 120 之间", exception.getMessage());
    }

    @Test
    void shouldRejectAgeGreaterThan120() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Student(
                "S001",
                "张三",
                121,
                Gender.MAN,
                "123456789"
        ));
        assertEquals("年龄必须在 0 到 120 之间", exception.getMessage());
    }

    @Test
    void shouldAcceptAgeZero() {
        Student student = new Student(
                "S001",
                "张三",
                0,
                Gender.MAN,
                "123456789"
        );

        assertEquals(0, student.getAge());
    }

    @Test
    void shouldAcceptAge120() {
        Student student = new Student(
                "S001",
                "张三",
                120,
                Gender.MAN,
                "123456789"
        );

        assertEquals(120, student.getAge());
    }

    @Test
    void shouldRejectNullCreatedAt() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Student(
                "S001",
                "张三",
                18,
                Gender.MAN,
                "123456789",
                null
        ));
        assertEquals("创建时间不能为空", exception.getMessage());
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(IllegalArgumentException.class, () -> new Student(
                null, "张三", 18, Gender.MAN, "123456789"
        ));
    }

    @Test
    void shouldRejectNullGender() {
        assertThrows(IllegalArgumentException.class, () -> new Student(
                "S001", "张三", 18, null, "123456789"
        ));
    }

    @Test
    void shouldRejectBlankPhone() {
        assertThrows(IllegalArgumentException.class, () -> new Student(
                "S001", "张三", 18, Gender.MAN, " "
        ));
    }

    @Test
    void shouldValidateUpdatedValues() {
        Student student = new Student(
                "S001", "张三", 18, Gender.MAN, "123456789"
        );

        assertThrows(IllegalArgumentException.class, () -> student.setName(" "));
        assertThrows(IllegalArgumentException.class, () -> student.setAge(121));
        assertThrows(IllegalArgumentException.class, () -> student.setGender(null));
        assertThrows(IllegalArgumentException.class, () -> student.setPhone(" "));
    }
}
