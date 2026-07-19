package com.freeloop.student.v4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileStudentRepositoryTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldCreateDataFile() {
        Path filePath = tempDir.resolve("data/students.csv");
        assertFalse(Files.exists(filePath));
        new FileStudentRepository(filePath);
        assertTrue(Files.exists(filePath));
    }

    @Test
    void shouldWriteStudentToFile() throws IOException {
        Path filePath = tempDir.resolve("data/students.csv");
        FileStudentRepository repository = new FileStudentRepository(filePath);
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 17, 10, 30);
        Student student = new Student(
                "S001",
                "张三",
                18,
                Gender.MAN,
                "123456789",
                createdAt
        );
        assertTrue(repository.add(student));
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        assertEquals(1, lines.size());
        assertEquals("S001,张三,18,MAN,123456789," + createdAt, lines.getFirst());
    }

    @Test
    void shouldReloadSavedStudent() {
        Path filePath = tempDir.resolve("data/students.csv");
        FileStudentRepository repository = new FileStudentRepository(filePath);
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 17, 10, 40);
        Student student = new Student(
                "S001",
                "张三",
                18,
                Gender.MAN,
                "123456789",
                createdAt
        );
        assertTrue(repository.add(student));
        FileStudentRepository reloadedRepository = new FileStudentRepository(filePath);
        Student reloaded = reloadedRepository.findById("S001");
        assertNotNull(reloaded);
        assertEquals(student.getId(), reloaded.getId());
        assertEquals(student.getName(), reloaded.getName());
        assertEquals(student.getAge(), reloaded.getAge());
        assertEquals(student.getGender(), reloaded.getGender());
        assertEquals(student.getPhone(), reloaded.getPhone());
        assertEquals(student.getCreatedAt(), reloaded.getCreatedAt());
    }

    @Test
    void shouldRejectInvalidStudentData() throws IOException {
        Path filePath = tempDir.resolve("data/students.csv");
        Files.createDirectories(filePath.getParent());
        Files.writeString(
                filePath,
                "S001,张三",
                StandardCharsets.UTF_8
        );
        assertThrows(
                StudentPersistenceException.class,
                () -> new FileStudentRepository(filePath)
        );
    }

    @Test
    void shouldRejectDuplicateIdsWhenLoading() throws IOException {
        Path filePath = tempDir.resolve("data/students.csv");
        Files.createDirectories(filePath.getParent());
        Files.write(
                filePath,
                List.of(
                        "S001,张三,18,Man,123456789,2026-07-17T10:30",
                        "S001,李四,20,Woman,987654321,2026-07-17T10:40"
                ),
                StandardCharsets.UTF_8
        );

        StudentPersistenceException exception = assertThrows(
                StudentPersistenceException.class,
                () -> new FileStudentRepository(filePath)
        );
        assertTrue(exception.getMessage().contains("重复学号"));
    }

    @Test
    void shouldReadLegacyGenderText() throws IOException {
        Path filePath = tempDir.resolve("data/students.csv");
        Files.createDirectories(filePath.getParent());
        Files.writeString(
                filePath,
                "S001,张三,18,Man,123456789,2026-07-17T10:30",
                StandardCharsets.UTF_8
        );

        FileStudentRepository repository = new FileStudentRepository(filePath);

        assertEquals(Gender.MAN, repository.findById("S001").getGender());
    }

    @Test
    void shouldRejectUnsupportedCsvCharactersAndRollback() {
        Path filePath = tempDir.resolve("data/students.csv");
        FileStudentRepository repository = new FileStudentRepository(filePath);
        Student student = new Student(
                "S001", "张三,同学", 18, Gender.MAN, "123456789"
        );

        assertThrows(StudentPersistenceException.class, () -> repository.add(student));
        assertNull(repository.findById("S001"));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldPersistUpdatedStudent() {
        Path filePath = tempDir.resolve("data/students.csv");
        FileStudentRepository repository = new FileStudentRepository(filePath);
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 17, 10, 50);
        Student originalStudent = new Student("S001",
                "张三",
                18,
                Gender.MAN,
                "123456789",
                createdAt);
        assertTrue(repository.add(originalStudent));
        Student updatedStudent = new Student("S001",
                "小红",
                20,
                Gender.WOMAN,
                "987456321",
                createdAt);
        assertTrue(repository.update(updatedStudent));
        FileStudentRepository reloadRepository = new FileStudentRepository(filePath);
        Student reloaded = reloadRepository.findById("S001");
        assertNotNull(reloaded);
        assertEquals(updatedStudent.getId(), reloaded.getId());
        assertEquals(updatedStudent.getName(), reloaded.getName());
        assertEquals(updatedStudent.getAge(), reloaded.getAge());
        assertEquals(updatedStudent.getGender(), reloaded.getGender());
        assertEquals(updatedStudent.getPhone(), reloaded.getPhone());
        assertEquals(updatedStudent.getCreatedAt(), reloaded.getCreatedAt());
    }

    @Test
    void shouldDeleteStudent() {
        Path filePath = tempDir.resolve("data/students.csv");
        FileStudentRepository repository = new FileStudentRepository(filePath);
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 17, 10, 40);
        Student originalStudent = new Student("S001",
                "张三",
                18,
                Gender.MAN,
                "123456789",
                createdAt);
        assertTrue(repository.add(originalStudent));
        assertTrue(repository.deleteById("S001"));

        FileStudentRepository reloadedRepository = new FileStudentRepository(filePath);
        assertNull(reloadedRepository.findById("S001"));
        assertTrue(reloadedRepository.findAll().isEmpty());
    }

    @Test
    void shouldRollbackAddWhenSavingFails() throws IOException {
        Path filePath = tempDir.resolve("data/students.csv");
        FileStudentRepository repository = new FileStudentRepository(filePath);
        blockDataFile(filePath);

        Student student = new Student(
                "S001", "张三", 18, Gender.MAN, "123456789"
        );

        assertThrows(StudentPersistenceException.class, () -> repository.add(student));
        assertNull(repository.findById("S001"));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldRollbackUpdateWhenSavingFails() throws IOException {
        Path filePath = tempDir.resolve("data/students.csv");
        FileStudentRepository repository = new FileStudentRepository(filePath);
        Student original = new Student(
                "S001", "张三", 18, Gender.MAN, "123456789"
        );
        assertTrue(repository.add(original));
        blockDataFile(filePath);

        Student updated = new Student(
                "S001", "李四", 20, Gender.WOMAN, "987654321",
                original.getCreatedAt()
        );

        assertThrows(StudentPersistenceException.class, () -> repository.update(updated));
        assertSame(original, repository.findById("S001"));
    }

    @Test
    void shouldRollbackDeleteWhenSavingFails() throws IOException {
        Path filePath = tempDir.resolve("data/students.csv");
        FileStudentRepository repository = new FileStudentRepository(filePath);
        Student student = new Student(
                "S001", "张三", 18, Gender.MAN, "123456789"
        );
        assertTrue(repository.add(student));
        blockDataFile(filePath);

        assertThrows(StudentPersistenceException.class, () -> repository.deleteById("S001"));
        assertSame(student, repository.findById("S001"));
    }

    private void blockDataFile(Path filePath) throws IOException {
        Files.delete(filePath);
        Files.createDirectory(filePath);
        Files.writeString(filePath.resolve("blocker.txt"), "block");
    }
}
