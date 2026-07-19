# Java 基础阶段总结

## 一、阶段目标

本阶段使用四周时间，从 Java 语法入门逐步完成一个可测试、可持久化的 Maven 学生管理系统。

最终产物：

- `student-management-java`：Maven 版学生管理系统。
- `docs/Java基础高频面试题30道.md`：基础知识复习材料。
- `docs/Controller-Service-Entity代码阅读.md`：分层代码阅读材料。
- `notes/week01-student-manager-v1` 至 `notes/week03-student-manager-v3`：前三周演进记录。

## 二、四周项目演进

### 第 1 周：数组版 v1

学习内容：

- 变量和基本类型
- `if`、`for`、`while`、`switch`
- 方法
- 类、对象、构造方法
- Getter、Setter 和基础数据校验

实现结果：

- 使用 `Student[10]` 保存学生。
- 使用 `size` 记录有效学生数量。
- 实现新增、列表、查询、修改和删除。
- 删除元素时手动移动数组并清空末尾位置。

主要认识：

数组可以帮助理解下标、容量和元素移动，但固定容量和手动维护 size 会增加复杂度。

### 第 2 周：集合与仓库版 v2

学习内容：

- 封装、继承和多态
- 抽象类与接口
- `ArrayList`、`HashMap`
- 泛型

实现结果：

- `Person` 抽取人员共有属性，`Student` 继承 Person。
- `Repository<T, ID>` 定义通用增删改查契约。
- 实现 ArrayList 和 HashMap 两种仓库。
- `StudentManagerApp` 只依赖 Repository 接口。

主要认识：

面向接口编程让调用方不必知道数据究竟保存在数组、List、Map、文件还是数据库中。泛型让 Repository 的实体类型和编号类型在编译期得到约束。

### 第 3 周：文件持久化与统计版 v3

学习内容：

- 异常体系与自定义异常
- NIO 文件读写和 UTF-8
- Stream
- `LocalDateTime`
- 枚举

实现结果：

- `FileStudentRepository` 启动时读取 CSV，数据变化后写回 CSV。
- `StudentPersistenceException` 统一表达持久化失败。
- `StudentStatisticsService` 通过 Stream 统计人数、平均年龄、最大年龄、最小年龄和性别数量。
- Student 使用 LocalDateTime 保存创建时间。
- Gender 枚举限制合法性别。

主要认识：

文件中的数据是上次运行留下的状态，而不是当前输入过程本身。异常不应该只在底层打印，而应保留原因并交给合适的上层决定如何提示。

### 第 4 周：Maven、测试、多线程和 JVM

学习内容：

- Maven 标准项目结构和 `pom.xml`
- JUnit Jupiter
- 线程、同步、原子变量、线程池、 Callable 和 Future
- JVM 栈、堆、GC、对象存活、JIT 和诊断命令

实现结果：

- 项目整理为 Maven 标准结构。
- 为实体、两个内存仓库、文件仓库、统计服务和控制台流程编写 55 个测试。
- 文件保存改为临时文件写入后替换，失败时回滚内存状态。
- HashMap 仓库使用 LinkedHashMap 保持稳定顺序。
- 并发与 JVM 实验放在独立包中，并标明不能当作业务实现。

主要认识：

测试不是为了证明代码永远没有问题，而是把已经确认的行为固定下来。多线程示例中的线程安全不会自动传递给业务仓库；JVM 的 OOM 和栈溢出实验必须单独运行。

## 三、核心知识的实际用途

### 集合

- 数组：容量固定、结构简单，适合练习下标和固定数量数据。
- ArrayList：适合需要顺序遍历和动态容量的数据。
- HashMap：适合通过唯一键快速定位对象。
- LinkedHashMap：同时需要键查询和稳定遍历顺序时使用。

学生系统中，学号是天然唯一键，因此 Map 适合按学号查询；统计时通过 `findAll()` 得到 List，再交给 Stream。

### 泛型

`Repository<T, ID>` 把“保存什么实体、使用什么编号”变成类型参数：

```java
Repository<Student, String> repository;
```

它避免为每一种实体复制一套接口，也防止把错误类型传入仓库。

### 异常

- 输入年龄不是数字：属于用户输入错误，可以提示后回到菜单。
- 学生参数为空：属于调用方违反对象约束，抛出 IllegalArgumentException。
- 文件不可读写：底层 IOException 被包装为 StudentPersistenceException。
- OOM 和 StackOverflowError：属于严重运行时错误，不能当作普通业务异常恢复。

### IO

项目通过 Path 和 Files 读写 UTF-8 文本。可靠持久化至少要考虑：

- 目录和文件是否存在。
- 编码是否一致。
- 文件内容是否损坏。
- 保存失败时内存和磁盘是否一致。
- 直接覆盖时程序中断是否会破坏原文件。
- 运行时数据是否应该进入 Git。

### Stream

Stream 适合对集合做转换和聚合：

```java
double averageAge = students.stream()
        .mapToInt(Student::getAge)
        .average()
        .orElse(0);
```

普通循环更适合需要下标、提前结束、原地修改和复杂控制流程的场景。Stream 不是循环的全面替代品。

## 四、项目分层理解

当前控制台项目可以映射到常见企业分层：

| 分层 | 当前类 | 职责 |
| --- | --- | --- |
| Entity | `Student`、`Person`、`Gender` | 表达领域数据和合法状态 |
| Repository | `Repository` 及三个实现 | 保存、查询、修改和删除数据 |
| Service | `StudentStatisticsService` | 完成统计业务 |
| Controller/App | `StudentManagerApp` | 接收输入、调用下层、输出结果 |

调用方向：

```text
用户输入
   -> StudentManagerApp
   -> Repository / StudentStatisticsService
   -> Student 或 CSV
```

上层可以调用下层，下层不应该反过来读取 Scanner 或控制菜单。

## 五、代码审查与重构总结

四周项目至少各进行一次 AI 辅助代码检查，主要改进如下：

| 阶段 | 主要问题 | 后续重构 |
| --- | --- | --- |
| v1 | main 过长、数组容量固定、命名不统一 | v2 引入方法拆分、集合和统一命名 |
| v2 | App 直接依赖具体集合、学号可修改 | 引入泛型 Repository，学号改为不可变 |
| v3 | 文件直接覆盖、写失败后内存可能已改变 | 最终版使用临时文件替换和失败回滚 |
| v4 | 测试边界不足、CSV 风险、HashMap 顺序不稳定 | 增加到 55 个测试，限制字段字符，使用 LinkedHashMap |

代码审查的价值不是让 AI 代替思考，而是帮助发现：

- 正常路径之外的失败场景。
- 类之间职责是否混乱。
- 文件、线程和资源是否存在隐蔽风险。
- 测试是否真正验证行为。

## 六、主要踩坑

1. 在 CMD 中输入 PowerShell 的 here-string，导致命令被当作程序名。
2. Git 暂存后继续修改，出现 `AM`，暂存区仍是旧内容。
3. GitHub 和本地同时修改同一文件，rebase 时产生冲突。
4. 增强 for 中删除集合元素可能触发 ConcurrentModificationException。
5. `Scanner.nextInt()` 与 `nextLine()` 混用会留下换行，最终统一读取字符串后解析。
6. 文件中的 Student 是历史数据，不需要在代码里手写固定对象。
7. `assertSame` 不能验证重新加载后的对象，因为反序列化会创建新实例。
8. Maven 必须在存在 `pom.xml` 的项目根目录运行。
9. `Thread.run()` 不会创建新线程，必须调用 `start()`。
10. GC 无法回收仍被 List 或静态字段引用的对象。
11. 相对路径取决于程序工作目录，最终统一使用模块下的 `data/students.csv`。
12. 运行时 CSV 不应进入 Git，使用 `.gitignore` 并取消已有跟踪。

## 七、测试与质量结果

- Maven 标准项目：已完成。
- JUnit 测试方法：55 个。
- 覆盖范围：
  - Student 构造与边界校验
  - ArrayList Repository CRUD
  - LinkedHashMap Repository CRUD、顺序和防御性复制
  - 文件创建、保存、重新加载、更新、删除
  - 错误 CSV、重复学号、旧枚举格式和写失败回滚
  - 统计正常数据、空列表和空参数
  - 控制台新增、更新、列表、查询、统计、删除和错误输入
- 文件保存：UTF-8、临时文件替换、失败回滚。
- Git：源码、测试和文档进入仓库；target 和运行时 CSV 被忽略。

## 八、当前能力自检

已经能够：

- 使用类和对象表达学生数据。
- 根据访问特点选择数组、List 或 Map。
- 使用泛型接口隔离调用方和实现方。
- 区分输入错误、参数错误和持久化异常。
- 使用 NIO 和 UTF-8 读写文件。
- 使用 Stream 完成统计和分组。
- 使用 Maven 管理标准目录、依赖和测试。
- 编写 JUnit 正常、边界和异常测试。
- 解释基本线程安全问题和 JVM 堆栈现象。
- 识别简单 Entity、Repository、Service 和 Controller 代码。

下一阶段需要继续加强：

- Spring Boot Web 项目结构。
- HTTP、REST 和参数校验。
- MySQL、JDBC、MyBatis。
- Service 事务与 Controller 异常处理。
- 集成测试和数据库测试。

## 九、最终验收命令

```powershell
cd "C:\Users\佘晨铭\Desktop\学习路线\enterprise-admin-system\student-management-java"
mvn clean test
```

预期：

```text
Tests run: 55, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Git 提交前：

```powershell
cd "C:\Users\佘晨铭\Desktop\学习路线\enterprise-admin-system"
git status
git add README.md .gitignore Java基础阶段总结.md docs student-management-java notes
git diff --cached --check
git commit -m "docs: complete java fundamentals stage"
git push
```
