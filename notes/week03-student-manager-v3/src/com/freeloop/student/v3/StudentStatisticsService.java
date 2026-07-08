package com.freeloop.student.v3;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentStatisticsService {
    public int count(List<Student> students) {
        return students.size();
    }

    public double averageAge(List<Student> students) {
        return students.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0);
    }

    public int maxAge(List<Student> students) {
        return students.stream()
                .mapToInt(Student::getAge)
                .max()
                .orElse(0);
    }

    public int minAge(List<Student> students) {
        return students.stream()
                .mapToInt(Student::getAge)
                .min()
                .orElse(0);
    }

    public Map<Gender, Long> countByGender(List<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getGender, Collectors.counting()));
    }
}
