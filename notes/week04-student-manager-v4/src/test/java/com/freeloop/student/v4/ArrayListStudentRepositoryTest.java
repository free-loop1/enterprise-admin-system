package com.freeloop.student.v4;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ArrayListStudentRepositoryTest {
    private ArrayListStudentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ArrayListStudentRepository();
    }

    @Test
    void shouldAddAndFindStudent() {
        Student student = new Student("S001",
                "张三",
                18,
                Gender.MAN,
                "123456789");
        boolean added = repository.add(student);
        Student found = repository.findById("S001");

        assertTrue(added);
        assertSame(student, found);
    }

    @Test
    void shouldRejectDuplicateId() {
        Student first = new Student("S001", "张三", 18, Gender.MAN, "123456789");
        Student duplicate = new Student("S001", "李四", 20, Gender.MAN, "987654321");
        assertTrue(repository.add(first));
        assertFalse(repository.add(duplicate));
        assertEquals(1, repository.findAll().size());
        assertSame(first, repository.findById("S001"));
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
    void shouldProtectRepositoryFromChangesToReturnedList(){
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
    void shouldRejectNullId() {
        assertThrows(NullPointerException.class, () -> repository.findById(null));
        assertThrows(NullPointerException.class, () -> repository.deleteById(null));
    }
}
