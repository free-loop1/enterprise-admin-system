package com.freeloop.student.v4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StudentStatisticsServiceTest {
    private StudentStatisticsService service;
    private List<Student> students;

    @BeforeEach
    void setUp() {
        service = new StudentStatisticsService();
        students = List.of(
                new Student("S001", "张三", 18, Gender.MAN, "13800000000"),
                new Student("S002", "李四", 20, Gender.WOMAN, "13900000000"),
                new Student("S003", "王五", 22, Gender.MAN, "13700000000")
        );
    }

    @Test
    void shouldCountStudents() {
        assertEquals(3, service.count(students));
    }

    @Test
    void shouldAverage() {
        assertEquals(20.0, service.averageAge(students), 0.001);
    }

    @Test
    void shouldMaxAge() {
        assertEquals(22, service.maxAge(students));
    }

    @Test
    void shouldMinAge() {
        assertEquals(18, service.minAge(students));
    }

    @Test
    void shouldCountByGender() {
        Map<Gender, Long> result = service.countByGender(students);
        assertEquals(2L, result.get(Gender.MAN));
        assertEquals(1L, result.get(Gender.WOMAN));
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnZeroValuesForEmptyList() {
        List<Student> emptyStudents = List.of();
        assertEquals(0, service.count(emptyStudents));
        assertEquals(0.0, service.averageAge(emptyStudents), 0.001);
        assertEquals(0, service.maxAge(emptyStudents));
        assertEquals(0, service.minAge(emptyStudents));
        Map<Gender, Long> result = service.countByGender(emptyStudents);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRejectNullStudentList() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.count(null)
        );
        assertEquals("学生列表不能为空", exception.getMessage());
    }

}
