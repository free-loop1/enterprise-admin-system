package com.freeloop.student.v4;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StudentStatisticsService {
    public int count(List<Student> students) {
        Objects.requireNonNull(students, "学生列表不能为空");
        return students.size();
    }

    public double averageAge(List<Student> students) {
        Objects.requireNonNull(students, "学生列表不能为空");
        return students.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0);
    }

    public int maxAge(List<Student> students) {
        Objects.requireNonNull(students, "学生列表不能为空");
        return students.stream()
                .mapToInt(Student::getAge)
                .max()
                .orElse(0);
    }

    public int minAge(List<Student> students) {
        Objects.requireNonNull(students, "学生列表不能为空");
        return students.stream()
                .mapToInt(Student::getAge)
                .min()
                .orElse(0);
    }

    public Map<Gender, Long> countByGender(List<Student> students) {
        Objects.requireNonNull(students, "学生列表不能为空");
        return students.stream()
                .collect(Collectors.groupingBy(Student::getGender, Collectors.counting()));
    }
}
