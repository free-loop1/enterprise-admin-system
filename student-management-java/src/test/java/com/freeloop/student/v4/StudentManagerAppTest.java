package com.freeloop.student.v4;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class StudentManagerAppTest {
    @Test
    void shouldUseModuleDataFileByDefault() {
        assertEquals(
                Path.of("data", "students.csv"),
                StudentManagerApp.resolveDataFile(new String[0])
        );
    }

    @Test
    void shouldUseDataFileFromCommandLineArgument() {
        Path customPath = Path.of("custom", "students.csv");
        assertEquals(
                customPath,
                StudentManagerApp.resolveDataFile(new String[]{customPath.toString()})
        );
    }

    @Test
    void shouldAddStudentFromInput() {
        Repository<Student, String> repository =
                new ArrayListStudentRepository();

        Scanner scanner = new Scanner("""
                1
                S001
                张三
                18
                Man
                13800000000
                0
                """);

        StudentManagerApp app =
                new StudentManagerApp(repository, scanner);

        app.run();

        Student saved = repository.findById("S001");

        assertNotNull(saved);
        assertEquals("S001", saved.getId());
        assertEquals("张三", saved.getName());
        assertEquals(18, saved.getAge());
        assertEquals(Gender.MAN, saved.getGender());
        assertEquals("13800000000", saved.getPhone());
    }

    @Test
    void shouldPreserveCreatedAtWhenUpdatingStudent() {
        Repository<Student, String> repository = new ArrayListStudentRepository();
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 17, 10, 0);
        Student original = new Student(
                "S001",
                "张三",
                18,
                Gender.MAN,
                "13800000000",
                createdAt
        );
        assertTrue(repository.add(original));
        Scanner scanner = new Scanner("""
                4
                S001
                小红
                20
                Woman
                13900000000
                0
                """);
        StudentManagerApp app = new StudentManagerApp(repository, scanner);
        app.run();
        Student updated = repository.findById("S001");
        assertNotNull(updated);
        assertEquals("小红", updated.getName());
        assertEquals(20, updated.getAge());
        assertEquals(Gender.WOMAN, updated.getGender());
        assertEquals("13900000000", updated.getPhone());
        assertEquals(createdAt, updated.getCreatedAt());
    }

    @Test
    void shouldNotAddStudentWhenAgeIsNotNumber() {
        Repository<Student, String> repository =
                new ArrayListStudentRepository();

        Scanner scanner = new Scanner("""
                1
                S001
                张三
                abc
                0
                """);

        StudentManagerApp app =
                new StudentManagerApp(repository, scanner);

        app.run();

        assertNull(repository.findById("S001"));
    }

    @Test
    void shouldRunListFindStatisticsAndDeleteWorkflow() {
        Repository<Student, String> repository = new ArrayListStudentRepository();
        assertTrue(repository.add(new Student(
                "S001", "张三", 18, Gender.MAN, "13800000000"
        )));
        Scanner scanner = new Scanner("""
                2
                3
                S001
                6
                5
                S001
                3
                S001
                0
                """);
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);

        new StudentManagerApp(repository, scanner, output).run();

        String text = outputBytes.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("身份：学生"));
        assertTrue(text.contains("学生总数：1"));
        assertTrue(text.contains("删除成功"));
        assertTrue(text.contains("没有该学生信息"));
        assertNull(repository.findById("S001"));
    }

    @Test
    void shouldReportInvalidMenuAndGender() {
        Repository<Student, String> repository = new ArrayListStudentRepository();
        Scanner scanner = new Scanner("""
                abc
                9
                1
                S001
                张三
                18
                unknown
                0
                """);
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);

        new StudentManagerApp(repository, scanner, output).run();

        String text = outputBytes.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("输入操作必须是数字"));
        assertTrue(text.contains("输入有错误"));
        assertTrue(text.contains("性别只能是 MAN 或 WOMAN"));
        assertTrue(repository.findAll().isEmpty());
    }
}
