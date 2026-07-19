# Student Management Java

这是 Java 基础阶段的最终项目，由学生管理系统 v1-v4 演进而来。第 4 周在 v3 的基础上加入 Maven、JUnit、多线程与 JVM 入门实验。

## 环境与命令

- JDK：25
- Maven：3.9+
- 测试框架：JUnit Jupiter 6

请在当前 `student-management-java` 目录执行：

```powershell
mvn clean test
```

主程序默认读写当前模块的 `data/students.csv`。也可以将其他路径作为第一个参数传入：

```powershell
java -cp target/classes com.freeloop.student.v4.StudentManagerApp data/students.csv
```

## 数据文件

`data/students.csv` 是程序运行时数据，不进入 Git。仓库启动时会自动创建目录和文件。

当前项目使用教学用的简化 CSV 格式，不支持学号、姓名或手机号包含逗号、双引号和换行符。保存时会拒绝这些字符，避免产生下次无法读取的文件。写入流程使用同目录临时文件并优先进行原子替换；保存失败时会回滚内存修改。

性别枚举使用 `MAN` 和 `WOMAN`。读取文件和控制台输入时不区分大小写，因此旧数据中的 `Man`、`Woman` 仍然兼容。

## 并发示例

`com.freeloop.student.v4.concurrent` 中的类用于分别演示线程创建、竞态条件、`synchronized`、`AtomicInteger`、线程池、`Callable`、`Future`、超时和取消。

这些示例不表示学生仓库支持并发访问。`ArrayListStudentRepository`、`HashMapStudentRepository` 和 `FileStudentRepository` 都是单线程教学实现，不应由多个线程同时修改。

## JVM 示例

`com.freeloop.student.v4.jvm` 只用于 JVM 教学实验，不属于学生管理业务代码。

以下程序必须单独运行，不能从业务代码调用：

- `HeapRetentionDemo`：故意制造 `OutOfMemoryError`，建议使用 `-Xms16m -Xmx16m`。
- `StackOverflowDemo`：故意制造 `StackOverflowError`。
- `JvmDiagnosticDemo`：保留约 5 MB 数据并等待 `jcmd` 诊断。
- `GarbageCollectionDemo`、`ObjectAgingDemo`：输出依赖垃圾收集器与 JVM 参数，不能把一次运行结果当作固定规律。
- `JitCompilationDemo`：只用于观察 JIT，不是正式性能基准；正式基准应使用 JMH。

即使示例捕获了 `OutOfMemoryError` 或 `StackOverflowError`，也不代表生产代码可以依靠捕获这些错误恢复运行。

## AI 代码审查与重构

| 审查发现 | 风险 | 重构结果 |
| --- | --- | --- |
| 内存修改后写文件失败 | 内存与磁盘状态不一致 | add、update、delete 保存失败时回滚 |
| 直接覆盖正式 CSV | 中断时可能留下空文件或半个文件 | 先写同目录临时文件，再原子替换 |
| 主程序路径依赖仓库根目录 | 从 Maven 模块启动时写错位置 | 默认使用 `data/students.csv`，支持参数覆盖 |
| 简单逗号分割不支持完整 CSV | 姓名含逗号后下次无法读取 | 保存前拒绝逗号、引号和换行 |
| 文件重复学号被静默忽略 | 数据损坏不易发现 | 加载时发现重复立即抛出异常 |
| HashMap 遍历顺序不稳定 | 列表和 CSV 顺序变化 | 使用 LinkedHashMap |
| App 输出写死到 System.out | 难以验证菜单提示 | 注入 PrintStream 并增加流程测试 |
| JVM 危险示例混在主源码 | 可能被误当作业务代码 | 独立 jvm 包、注释和 README 警告 |

最终共有 55 个 JUnit 测试方法，覆盖实体校验、三种仓库、文件异常与回滚、统计功能和完整菜单流程。
