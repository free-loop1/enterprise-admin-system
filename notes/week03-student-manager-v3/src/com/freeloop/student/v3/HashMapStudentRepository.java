package com.freeloop.student.v3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HashMapStudentRepository implements Repository<Student, String> {
    private final Map<String, Student> students = new HashMap<>();

    @Override
    public boolean add(Student entity) {
        Objects.requireNonNull(entity, "学生不能为空");
        return students.putIfAbsent(entity.getId(), entity) == null;
    }

    @Override
    public Student findById(String id) {
        return students.get(id);
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }

    @Override
    public boolean update(Student entity) {
        Objects.requireNonNull(entity, "学生不能为空");
        if (!students.containsKey(entity.getId())) {
            return false;
        }
        students.put(entity.getId(), entity);
        return true;
    }

    @Override
    public boolean deleteById(String id) {
        return students.remove(id) != null;
    }

}
