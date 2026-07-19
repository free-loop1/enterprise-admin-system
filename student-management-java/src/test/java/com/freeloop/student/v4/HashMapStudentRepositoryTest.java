package com.freeloop.student.v4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashMapStudentRepositoryTest {
    private HashMapStudentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new HashMapStudentRepository();
    }

    @Test
    void shouldAddAndFindStudent() {
        Student student = new Student("S001", "张三", 18, Gender.MAN, "123456789");
        assertTrue(repository.add(student));
        assertSame(student, repository.findById("S001"));
    }

    @Test
    void shouldRejectDuplicateId() {
        Student student = new Student("S001", "张三", 18, Gender.MAN, "123456789");
        assertTrue(repository.add(student));
        Student duplicate = new Student("S001", "小红", 20, Gender.WOMAN, "987456321");
        assertFalse(repository.add(duplicate));
        assertEquals(1, repository.findAll().size());
        assertSame(student, repository.findById("S001"));
    }

    @Test
    void shouldUpdateExistingStudent() {
        Student original = new Student("S001", "张三", 18, Gender.MAN, "123456789");
        repository.add(original);
        Student updated = new Student("S001", "张三", 20, Gender.MAN, "123456789");
        assertTrue(repository.update(updated));
        assertSame(updated, repository.findById("S001"));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldReturnFalseWhenUpdatingMissingStudent() {
        Student missing = new Student("S001", "张三", 20, Gender.MAN, "123456789");
        assertFalse(repository.update(missing));
        assertNull(repository.findById("S001"));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldDeleteExistingStudent() {
        Student original = new Student("S001", "张三", 18, Gender.MAN, "123456789");
        repository.add(original);
        assertTrue(repository.deleteById("S001"));
        assertNull(repository.findById("S001"));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingStudent() {
        assertFalse(repository.deleteById("S404"));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldProtectRepositoryFromChangesToReturnedList() {
        Student student = new Student(
                "S001", "张三", 18, Gender.MAN, "123456789"
        );
        repository.add(student);
        List<Student> returnedStudents = repository.findAll();
        returnedStudents.clear();
        assertTrue(returnedStudents.isEmpty());
        assertEquals(1, repository.findAll().size());
        assertSame(student, repository.findById("S001"));
    }

    @Test
    void shouldKeepInsertionOrder() {
        Student first = new Student("S002", "李四", 20, Gender.MAN, "123");
        Student second = new Student("S001", "张三", 18, Gender.WOMAN, "456");
        repository.add(first);
        repository.add(second);

        assertEquals(List.of(first, second), repository.findAll());
    }

    @Test
    void shouldRejectNullId() {
        assertThrows(NullPointerException.class, () -> repository.findById(null));
        assertThrows(NullPointerException.class, () -> repository.deleteById(null));
    }
}
