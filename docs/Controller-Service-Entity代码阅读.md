# Controller、Service、Entity 代码阅读

## 一、先看一次完整请求流程

企业项目通常把代码分成不同职责：

```text
请求或用户输入
    -> Controller
    -> Service
    -> Repository
    -> Entity / Database
```

- Controller：接收外部输入、调用 Service、返回结果。
- Service：组织业务规则和业务流程。
- Repository：访问和保存数据。
- Entity：表达业务数据。

当前项目是控制台程序，不是 Spring Web 项目，但已经具有同样的职责雏形。

## 二、在当前项目中识别分层

### 1. Entity：Student

文件：`student-management-java/src/main/java/com/freeloop/student/v4/Student.java`

阅读重点：

```java
public class Student extends Person {
    private final String id;
    private final LocalDateTime createdAt;
}
```

这段代码说明：

- Student 是一个领域实体。
- 它继承 Person 的姓名、年龄、性别和手机号。
- 学号使用 final，创建后不能改变。
- 创建时间使用 LocalDateTime，而不是随意拼接的字符串。
- 构造方法负责保证对象一开始就是合法状态。

Entity 不应该负责读取 Scanner、打印菜单或直接写 CSV。

### 2. Repository：Repository 与 FileStudentRepository

文件：`student-management-java/src/main/java/com/freeloop/student/v4/Repository.java`

```java
public interface Repository<T, ID> {
    boolean add(T entity);
    T findById(ID id);
    List<T> findAll();
    boolean update(T entity);
    boolean deleteById(ID id);
}
```

阅读重点：

- T 表示实体类型，ID 表示编号类型。
- 接口只定义能力，不关心数据具体放在哪里。
- `Repository<Student, String>` 表示保存 Student，并使用 String 学号查询。

`FileStudentRepository` 是具体实现。它负责把 Student 转换为文件内容，而不是负责菜单或统计。

### 3. Service：StudentStatisticsService

文件：`student-management-java/src/main/java/com/freeloop/student/v4/StudentStatisticsService.java`

```java
public double averageAge(List<Student> students) {
    Objects.requireNonNull(students, "学生列表不能为空");
    return students.stream()
            .mapToInt(Student::getAge)
            .average()
            .orElse(0);
}
```

阅读重点：

- 方法接收已经准备好的业务数据。
- Service 不读取控制台，也不决定文件路径。
- 它集中实现统计规则，便于单独测试。
- 空集合平均年龄返回 0，空引用则被明确拒绝。

### 4. Controller/App：StudentManagerApp

文件：`student-management-java/src/main/java/com/freeloop/student/v4/StudentManagerApp.java`

```java
private void deleteStudent() {
    String id = readLine("输入需要删除的学生学号");
    if (repository.deleteById(id)) {
        output.println("删除成功");
    } else {
        output.println("没有该学生信息");
    }
}
```

阅读重点：

1. 从外部读取学号。
2. 调用 Repository。
3. 根据返回结果输出不同信息。

它不应该自己遍历 HashMap 或直接修改 CSV。这里的职责与 Web 项目中的 Controller 很接近，只是输入来自 Scanner，输出写到 PrintStream。

## 三、看懂简单 Spring 风格代码

下面是用于阅读的示例，不属于当前项目编译源码：

```java
@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public StudentResponse findById(@PathVariable String id) {
        return studentService.findById(id);
    }
}
```

逐行理解：

- `@RestController`：这个类接收 HTTP 请求，返回值会写入 HTTP 响应。
- `@RequestMapping("/students")`：统一路径前缀。
- `private final StudentService`：Controller 依赖 Service，不直接访问数据库。
- 构造方法：由 Spring 注入 StudentService。
- `@GetMapping("/{id}")`：接收 GET `/students/S001`。
- `@PathVariable String id`：把路径中的 S001 传给参数 id。
- `studentService.findById(id)`：业务查询交给 Service。
- `StudentResponse`：返回给调用方的数据结构。

对应 Service：

```java
@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public StudentResponse findById(String id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        return StudentResponse.from(student);
    }
}
```

逐行理解：

- `@Service`：声明业务服务。
- Service 依赖 Repository。
- Repository 找不到数据时，Service 抛出业务异常。
- Service 把 Entity 转换为 Response，避免把数据库实体直接暴露给外部。

对应 Entity：

```java
@Entity
@Table(name = "student")
public class StudentEntity {
    @Id
    private String id;
    private String name;
    private Integer age;
}
```

逐行理解：

- `@Entity`：这是一个持久化实体。
- `@Table`：映射数据库表。
- `@Id`：id 是主键。
- 字段通常映射为表中的列。

## 四、常见职责错误

### Controller 直接写 SQL

问题：Controller 同时负责协议、业务和数据库，难以测试和复用。

正确方向：Controller 调用 Service，Service 调用 Repository。

### Entity 读取 Scanner

问题：实体依赖具体输入方式，Web、测试或批处理无法复用。

正确方向：Controller 读取输入，构造或更新 Entity。

### Repository 负责统计展示

问题：数据访问、业务计算和界面输出混在一起。

正确方向：Repository 只提供数据，Service 统计，Controller 展示。

### Service 返回 `System.out.println`

问题：业务层绑定控制台，无法用于 HTTP 接口。

正确方向：Service 返回对象或抛出异常，由 Controller 决定输出形式。

## 五、自测题与答案

### 1. 用户访问 `/students/S001` 时，谁最先接收请求？

答案：Controller。

### 2. “学号不存在时抛出业务异常”应该主要放在哪一层？

答案：Service，因为这是业务流程规则。

### 3. “执行 SELECT 查询”应该放在哪一层？

答案：Repository。

### 4. Student 的姓名和年龄属于哪一层的数据？

答案：Entity 或领域模型。

### 5. Controller 能否直接依赖 Repository？

简单查询在小项目中可以，但企业项目通常通过 Service，避免业务逻辑散落在 Controller。

### 6. 为什么 Controller 返回 Response，而不是一定返回 Entity？

数据库实体结构和接口结构可能不同。Response 可以隐藏内部字段、组合展示数据并保持接口稳定。

### 7. 当前项目的 StudentManagerApp 为什么只是“类似 Controller”？

因为它接收的是控制台输入而不是 HTTP 请求，也没有使用 Spring MVC 注解，但它承担了输入、调用下层和输出结果的协调职责。

## 六、阅读新项目的固定顺序

看到一个陌生 Controller 时，可以按以下顺序：

1. 看类上的请求路径。
2. 看方法处理什么 HTTP 动作。
3. 看输入来自路径、查询参数还是请求体。
4. 看它调用哪个 Service 方法。
5. 跳到 Service 看业务规则和异常。
6. 跳到 Repository 看数据从哪里获取。
7. 看 Entity、DTO 和 Response 分别有哪些字段。
8. 最后画出调用链，不要一开始逐行陷入细节。
