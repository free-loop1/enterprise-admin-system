package com.freeloop.student.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArrayListStudentRepository implements Repository<Student, String> {
    private final List<Student> students = new ArrayList<>();

    @Override
    public boolean add(Student entity) {
        Objects.requireNonNull(entity, "学生不能为空");
        if (findById(entity.getId()) != null) {
            return false;
        }
        students.add(entity);
        return true;
    }

    @Override
    public Student findById(String id) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    @Override
    public boolean update(Student entity) {
        Objects.requireNonNull(entity, "学生不能为空");
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(entity.getId())) {
                students.set(i, entity);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(id)) {
                students.remove(i);
                return true;
            }
        }
        return false;
    }
}
