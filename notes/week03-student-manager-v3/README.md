# 第 3 周：学生管理系统 v3

## 本周目标

- 学习异常处理，能区分普通输入错误和文件读写错误。
- 学习 IO，把学生数据保存到 `students.csv`，程序重启后可以重新读取。
- 学习 Stream，用声明式方式完成统计功能。
- 学习日期时间 API，用 `LocalDateTime` 记录学生创建时间。
- 学习枚举，用 `Gender` 限制性别取值。

## 已实现功能

- 添加学生
- 查看学生列表
- 根据学号查询学生
- 修改学生信息
- 删除学生信息
- 统计学生总数、平均年龄、最大年龄、最小年龄、按性别统计
- 使用 `FileStudentRepository` 将数据持久化到 `data/students.csv`

## 主要类说明

- `StudentManagerApp`：控制台入口，负责读取用户输入和调用仓库。
- `Repository<T, ID>`：通用仓库接口，定义增删改查行为。
- `HashMapStudentRepository`：基于 `HashMap<String, Student>` 的内存仓库。
- `FileStudentRepository`：文件仓库，对外仍然实现 `Repository<Student, String>`，内部委托内存仓库并在变更后保存文件。
- `StudentStatisticsService`：统计服务，集中处理 Stream 统计逻辑。
- `StudentPersistenceException`：自定义运行时异常，用于表达学生数据持久化失败。
- `Gender`：枚举，限制性别只能是 `Man` 或 `Woman`。

## 学到的知识点

- 自定义异常类通过继承 `RuntimeException` 表达业务层面的失败原因。
- `super(message)` 会把错误信息传给父类异常，方便上层统一读取 `e.getMessage()`。
- 文件仓库可以包一层内存仓库：查询从内存读，新增、修改、删除成功后再写回文件。
- `Files.readAllLines(path, StandardCharsets.UTF_8)` 和 `Files.write(path, lines, StandardCharsets.UTF_8)` 可以避免平台默认编码不一致。
- `line.split(",", -1)` 会保留末尾空字段，适合做 CSV 字段数量校验。
- Stream 适合做统计、分组、聚合，不适合替代所有普通循环。
- 日期时间字段用 `LocalDateTime` 表达创建时间，比字符串更适合在程序内部处理。

## 踩坑与解决

- 坑：文件中的学生数据不是用户实时输入，而是上次运行保存下来的历史数据。
  解决：程序启动时 `loadFromFile()` 读取文件，每次新增、修改、删除后 `saveToFile()` 写回文件。
- 坑：把统计方法加到 `Repository` 接口会让仓库职责变乱。
  解决：统计逻辑放到 `StudentStatisticsService`，仓库只负责数据访问。
- 坑：增强 for 循环中直接删除集合元素不安全。
  解决：需要按索引修改或删除时使用普通 `for` 循环；只读取时使用增强 for。
- 坑：文件读写异常不能只打印错误。
  解决：封装成 `StudentPersistenceException` 抛给上层，由入口统一提示。

## 验收清单

- 能编译第 3 周所有 Java 文件。
- 能添加、查询、修改、删除学生。
- 能把学生数据写入 `students.csv`。
- 程序重启后能从 `students.csv` 读取学生数据。
- 能输出学生统计信息。
- 错误格式的 CSV 数据会抛出 `StudentPersistenceException`。
