package com.freeloop.student.v4;

import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class StudentManagerApp {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "students.csv");

    private final Repository<Student, String> repository;
    private final Scanner scanner;
    private final PrintStream output;

    public StudentManagerApp(Repository<Student, String> repository, Scanner scanner) {
        this(repository, scanner, System.out);
    }

    StudentManagerApp(
            Repository<Student, String> repository,
            Scanner scanner,
            PrintStream output
    ) {
        this.repository = Objects.requireNonNull(repository, "仓库不能为空");
        this.scanner = Objects.requireNonNull(scanner, "输入流不能为空");
        this.output = Objects.requireNonNull(output, "输出流不能为空");
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Repository<Student, String> repository = new FileStudentRepository(
                    resolveDataFile(args)
            );
            new StudentManagerApp(repository, scanner).run();
        } catch (StudentPersistenceException e) {
            System.out.println("学生数据读写失败：" + e.getMessage());
        }
    }

    static Path resolveDataFile(String[] args) {
        if (args.length == 0) {
            return DEFAULT_DATA_FILE;
        }
        return Path.of(args[0]);
    }

    public void run() {
        while (true) {
            printMenu();

            String chooseText = scanner.nextLine();
            int choose;
            try {
                choose = Integer.parseInt(chooseText);
            } catch (NumberFormatException e) {
                output.println("输入操作必须是数字");
                continue;
            }

            switch (choose) {
                case 0:
                    return;
                case 1:
                    addStudent();
                    break;
                case 2:
                    listStudents();
                    break;
                case 3:
                    findStudent();
                    break;
                case 4:
                    updateStudent();
                    break;
                case 5:
                    deleteStudent();
                    break;
                case 6:
                    printStatistics();
                    break;
                default:
                    output.println("输入有错误");
            }
        }
    }

    private void addStudent() {
        String id = readLine("输入学生学号");
        Student student = readStudent(id);
        if (student == null) {
            return;
        }

        if (repository.add(student)) {
            output.println("学生添加成功");
        } else {
            output.println("学号重复，添加失败");
        }
    }

    private void listStudents() {
        List<Student> students = repository.findAll();
        if (students.isEmpty()) {
            output.println("没有学生信息");
            return;
        }

        for (Student student : students) {
            printPerson(student);
        }
    }

    private void findStudent() {
        String id = readLine("输入学生学号");
        Student student = repository.findById(id);
        if (student == null) {
            output.println("没有该学生信息");
            return;
        }
        printPerson(student);
    }

    private void updateStudent() {
        String id = readLine("输入学生学号");
        Student existingStudent = repository.findById(id);
        if (existingStudent == null) {
            output.println("没有该学生信息");
            return;
        }

        Student updatedStudent = readStudent(id, existingStudent.getCreatedAt());
        if (updatedStudent == null) {
            return;
        }

        if (repository.update(updatedStudent)) {
            output.println("更新成功");
        } else {
            output.println("更新失败");
        }
    }

    private void deleteStudent() {
        String id = readLine("输入需要删除的学生学号");
        if (repository.deleteById(id)) {
            output.println("删除成功");
        } else {
            output.println("没有该学生信息");
        }
    }

    private void printStatistics() {
        List<Student> students = repository.findAll();
        StudentStatisticsService statisticsService = new StudentStatisticsService();
        output.println("学生总数：" + statisticsService.count(students));
        output.println("平均年龄：" + statisticsService.averageAge(students));
        output.println("最大年龄：" + statisticsService.maxAge(students));
        output.println("最小年龄：" + statisticsService.minAge(students));
        output.println("按性别统计：" + statisticsService.countByGender(students));
    }

    private Student readStudent(String id) {
        return readStudent(id, null);
    }

    private Student readStudent(String id, LocalDateTime createdAt) {
        String name = readLine("输入学生姓名");
        Integer age = readAge();
        if (age == null) {
            return null;
        }
        String genderText = readLine("输入学生性别 MAN 或 WOMAN");
        Gender gender;
        try {
            gender = Gender.fromText(genderText);
        } catch (IllegalArgumentException e) {
            output.println("性别只能是 MAN 或 WOMAN");
            return null;
        }
        String phone = readLine("输入学生手机号");

        try {
            if (createdAt == null) {
                return new Student(id, name, age, gender, phone);
            }
            return new Student(id, name, age, gender, phone, createdAt);
        } catch (IllegalArgumentException e) {
            output.println(e.getMessage());
            return null;
        }
    }

    private Integer readAge() {
        String ageText = readLine("输入学生年龄");
        try {
            return Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            output.println("年龄必须是数字");
            return null;
        }
    }

    private String readLine(String prompt) {
        output.println(prompt);
        return scanner.nextLine();
    }

    private void printMenu() {
        output.println("""
                输入需要的操作：
                0：退出操作
                1：添加学生信息
                2：查看学生列表
                3：根据学号查询学生
                4：修改学生信息
                5：删除学生信息
                6：统计学生信息
                """);
    }

    private void printPerson(Person person) {
        output.println("身份：" + person.getRole());
        output.println(person);
    }
}
