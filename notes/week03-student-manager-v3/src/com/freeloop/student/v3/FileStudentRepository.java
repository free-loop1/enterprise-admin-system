package com.freeloop.student.v3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class FileStudentRepository implements Repository<Student, String> {
    private static final int STUDENT_FIELD_COUNT = 6;

    private final Path filePath;
    private final Repository<Student, String> memoryRepository = new HashMapStudentRepository();

    public FileStudentRepository(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "文件路径不能为空");
        initFile();
        loadFromFile();
    }

    @Override
    public boolean add(Student student) {
        Objects.requireNonNull(student, "学生不能为空");
        if (findById(student.getId()) != null) {
            return false;
        }
        memoryRepository.add(student);
        saveToFile();
        return true;
    }

    @Override
    public Student findById(String id) {
        return memoryRepository.findById(id);
    }

    @Override
    public List<Student> findAll() {
        return memoryRepository.findAll();
    }

    @Override
    public boolean update(Student student) {
        boolean updated = memoryRepository.update(student);
        if (updated) {
            saveToFile();
        }
        return updated;
    }

    @Override
    public boolean deleteById(String id) {
        boolean deleted = memoryRepository.deleteById(id);
        if (deleted) {
            saveToFile();
        }
        return deleted;
    }

    private void initFile() {
        try {
            Path parent = filePath.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new StudentPersistenceException("学生数据文件初始化失败", e);
        }
    }

    private void loadFromFile() {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                Student student = parseStudent(line);
                memoryRepository.add(student);
            }
        } catch (IOException e) {
            throw new StudentPersistenceException("读取学生数据文件失败", e);
        }
    }

    private Student parseStudent(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != STUDENT_FIELD_COUNT) {
            throw new StudentPersistenceException("学生数据格式错误：" + line);
        }
        try {
            String id = parts[0];
            String name = parts[1];
            int age = Integer.parseInt(parts[2]);
            Gender gender = Gender.valueOf(parts[3]);
            String phone = parts[4];
            LocalDateTime createdAt = LocalDateTime.parse(parts[5]);
            return new Student(id, name, age, gender, phone, createdAt);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new StudentPersistenceException("学生数据内容错误：" + line, e);
        }
    }

    private void saveToFile() {
        try {
            List<String> lines = memoryRepository.findAll()
                    .stream()
                    .map(this::formatStudent)
                    .toList();

            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StudentPersistenceException("保存学生数据文件失败", e);
        }
    }

    private String formatStudent(Student student) {
        return student.getId() + ","
                + student.getName() + ","
                + student.getAge() + ","
                + student.getGender() + ","
                + student.getPhone() + ","
                + student.getCreatedAt();
    }
}
