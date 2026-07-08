package com.freeloop.student.v3;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class StudentManagerApp {
    private final Repository<Student, String> repository;
    private final Scanner scanner;

    public StudentManagerApp(Repository<Student, String> repository, Scanner scanner) {
        this.repository = Objects.requireNonNull(repository, "仓库不能为空");
        this.scanner = Objects.requireNonNull(scanner, "输入流不能为空");
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Repository<Student, String> repository = new FileStudentRepository(Path.of("notes/week03-student-manager-v3/data/students.csv"));
            new StudentManagerApp(repository, scanner).run();
        } catch (StudentPersistenceException e) {
            System.out.println("学生数据读写失败：" + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            printMenu();

            String chooseText = scanner.nextLine();
            int choose;
            try {
                choose = Integer.parseInt(chooseText);
            } catch (NumberFormatException e) {
                System.out.println("输入操作必须是数字");
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
                    System.out.println("输入有错误");
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
            System.out.println("学生添加成功");
        } else {
            System.out.println("学号重复，添加失败");
        }
    }

    private void listStudents() {
        List<Student> students = repository.findAll();
        if (students.isEmpty()) {
            System.out.println("没有学生信息");
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
            System.out.println("没有该学生信息");
            return;
        }
        printPerson(student);
    }

    private void updateStudent() {
        String id = readLine("输入学生学号");
        if (repository.findById(id) == null) {
            System.out.println("没有该学生信息");
            return;
        }

        Student updatedStudent = readStudent(id);
        if (updatedStudent == null) {
            return;
        }

        if (repository.update(updatedStudent)) {
            System.out.println("更新成功");
        } else {
            System.out.println("更新失败");
        }
    }

    private void deleteStudent() {
        String id = readLine("输入需要删除的学生学号");
        if (repository.deleteById(id)) {
            System.out.println("删除成功");
        } else {
            System.out.println("没有该学生信息");
        }
    }

    private void printStatistics() {
        List<Student> students = repository.findAll();
        StudentStatisticsService statisticsService = new StudentStatisticsService();
        System.out.println("学生总数：" + statisticsService.count(students));
        System.out.println("平均年龄：" + statisticsService.averageAge(students));
        System.out.println("最大年龄：" + statisticsService.maxAge(students));
        System.out.println("最小年龄：" + statisticsService.minAge(students));
        System.out.println("按性别统计：" + statisticsService.countByGender(students));
    }

    private Student readStudent(String id) {
        String name = readLine("输入学生姓名");
        Integer age = readAge();
        if (age == null) {
            return null;
        }
        String genderText = readLine("输入学生性别 Man 或 Woman");
        Gender gender;
        try {
            gender = Gender.valueOf(genderText);
        } catch (IllegalArgumentException e) {
            System.out.println("性别只能是 Man 或 Woman");
            return null;
        }
        String phone = readLine("输入学生手机号");

        try {
            return new Student(id, name, age, gender, phone);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Integer readAge() {
        String ageText = readLine("输入学生年龄");
        try {
            return Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            System.out.println("年龄必须是数字");
            return null;
        }
    }

    private String readLine(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    private static void printMenu() {
        System.out.println("""
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

    private static void printPerson(Person person) {
        System.out.println("身份：" + person.getRole());
        System.out.println(person);
    }
}
