package com.freeloop.student.v4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class FileStudentRepository implements Repository<Student, String> {
    private static final int STUDENT_FIELD_COUNT = 6;

    private final Path filePath;
    private final Repository<Student, String> memoryRepository = new HashMapStudentRepository();

    public FileStudentRepository(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "文件路径不能为空")
                .toAbsolutePath()
                .normalize();
        initFile();
        loadFromFile();
    }

    @Override
    public boolean add(Student student) {
        Objects.requireNonNull(student, "学生不能为空");
        if (!memoryRepository.add(student)) {
            return false;
        }
        try {
            saveToFile();
        } catch (StudentPersistenceException e) {
            memoryRepository.deleteById(student.getId());
            throw e;
        }
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
        Objects.requireNonNull(student, "学生不能为空");
        Student previousStudent = memoryRepository.findById(student.getId());
        if (previousStudent == null) {
            return false;
        }

        memoryRepository.update(student);
        try {
            saveToFile();
        } catch (StudentPersistenceException e) {
            memoryRepository.update(previousStudent);
            throw e;
        }
        return true;
    }

    @Override
    public boolean deleteById(String id) {
        Student deletedStudent = memoryRepository.findById(id);
        if (deletedStudent == null) {
            return false;
        }

        memoryRepository.deleteById(id);
        try {
            saveToFile();
        } catch (StudentPersistenceException e) {
            memoryRepository.add(deletedStudent);
            throw e;
        }
        return true;
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
                if (!memoryRepository.add(student)) {
                    throw new StudentPersistenceException(
                            "学生数据存在重复学号：" + student.getId()
                    );
                }
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
            Gender gender = Gender.fromText(parts[3]);
            String phone = parts[4];
            LocalDateTime createdAt = LocalDateTime.parse(parts[5]);
            return new Student(id, name, age, gender, phone, createdAt);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new StudentPersistenceException("学生数据内容错误：" + line, e);
        }
    }

    private void saveToFile() {
        Path temporaryFile = null;
        try {
            List<String> lines = memoryRepository.findAll()
                    .stream()
                    .map(this::formatStudent)
                    .toList();

            temporaryFile = Files.createTempFile(
                    filePath.getParent(),
                    filePath.getFileName().toString(),
                    ".tmp"
            );
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile);
        } catch (IOException e) {
            throw new StudentPersistenceException("保存学生数据文件失败", e);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    filePath,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(
                    temporaryFile,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
            // A failed cleanup must not hide the original persistence failure.
        }
    }

    private String formatStudent(Student student) {
        return requireSimpleCsvField("学号", student.getId()) + ","
                + requireSimpleCsvField("姓名", student.getName()) + ","
                + student.getAge() + ","
                + student.getGender() + ","
                + requireSimpleCsvField("手机号", student.getPhone()) + ","
                + student.getCreatedAt();
    }

    private String requireSimpleCsvField(String fieldName, String value) {
        if (value.indexOf(',') >= 0
                || value.indexOf('"') >= 0
                || value.indexOf('\r') >= 0
                || value.indexOf('\n') >= 0) {
            throw new StudentPersistenceException(
                    fieldName + "不能包含逗号、双引号或换行符"
            );
        }
        return value;
    }
}
