# Java 基础高频面试题 30 道

## 一、语法与面向对象

### 1. Java 的基本类型和引用类型有什么区别？

基本类型直接保存值，包括 `byte`、`short`、`int`、`long`、`float`、`double`、`char` 和 `boolean`。引用类型变量保存的是对象引用，例如 `String`、数组、集合和自定义类。

局部变量中的基本值和对象引用都属于当前栈帧的一部分，对象本身通常位于堆中。学生系统里的 `int age` 是基本类型，`Student student` 是引用类型。

### 2. `==` 和 `equals()` 有什么区别？

对基本类型使用 `==` 比较值；对引用类型使用 `==` 比较是否指向同一个对象。`equals()` 用于比较对象的业务内容，但具体规则由类实现。

`String` 已经重写 `equals()`，因此比较学号应写 `student.getId().equals(id)`，不能依赖 `==`。

### 3. Java 是值传递还是引用传递？

Java 只有值传递。传递基本类型时复制基本值；传递对象时复制引用的值。方法可以通过复制后的引用修改同一个对象，但不能让调用者的变量改为指向另一个对象。

把 `Student` 传给打印方法时，复制的是引用。方法调用 `student.setAge(20)` 会影响原对象，但执行 `student = new Student(...)` 不会改变调用者变量。

### 4. 方法重载和方法重写有什么区别？

重载发生在同一个类中：方法名相同、参数列表不同，编译期决定调用哪个方法。重写发生在父子类之间：子类重新实现父类方法，运行期根据对象实际类型选择实现。

`Student` 重写 `Person.getRole()` 是多态；提供带创建时间和不带创建时间的两个 `Student` 构造方法属于重载。

### 5. 封装、继承、多态、抽象类和接口分别解决什么问题？

- 封装隐藏内部状态，通过方法维护合法数据。
- 继承复用父类已有属性和行为。
- 多态让同一个父类型或接口变量表示不同实现。
- 抽象类表达“具有共同状态但不能直接创建”的父类型。
- 接口定义调用方和实现方都要遵守的行为契约。

项目中 `Person` 是抽象类，`Student` 继承它；`Repository<T, ID>` 是接口；App 依赖 Repository 接口，因此可以替换 ArrayList、HashMap 或文件实现。

## 二、集合与泛型

### 6. 数组和 `ArrayList` 有什么区别？

数组创建后长度固定，可以保存基本类型或对象；`ArrayList` 长度动态变化，只能保存对象类型，并提供 `add`、`remove`、`contains` 等集合操作。

v1 使用 `Student[10]`，必须手动维护 `size` 和移动元素；v2 使用 `ArrayList<Student>` 后不再受固定容量限制。

### 7. `ArrayList` 和 `LinkedList` 应该如何选择？

`ArrayList` 基于连续动态数组，按下标访问快，尾部添加通常快；中间插入或删除需要移动元素。`LinkedList` 基于双向链表，已知节点位置时插入删除方便，但按下标查找需要遍历且额外占用节点指针空间。

学生系统主要进行遍历和按学号查找，没有频繁的链表中间插入，因此 `ArrayList` 更合适。

### 8. `HashMap` 的作用和基本原理是什么？

`HashMap` 使用键值对保存数据。它先根据键的 `hashCode()` 定位桶，再使用 `equals()` 确认具体键；发生哈希冲突时，同一桶中会保存多个节点。查询、添加和删除的平均时间复杂度接近 `O(1)`。

使用 `Map<String, Student>` 后，学号是键，可以直接按学号找到学生，不需要像 ArrayList 一样遍历。

### 9. 为什么重写 `equals()` 时通常也必须重写 `hashCode()`？

Java 约定：两个对象如果 `equals()` 相等，它们的 `hashCode()` 必须相等。否则 HashMap 或 HashSet 可能把逻辑相同的对象放进不同桶，导致查询或去重失败。

当前项目直接使用不可变 `String` 学号作为 Map 键，String 已正确实现这两个方法，因此无需让可变 Student 直接作为键。

### 10. 为什么不能在增强 `for` 循环中直接删除集合元素？

增强 `for` 通常通过迭代器遍历。遍历期间直接调用集合的结构修改方法，会让迭代器检测到修改计数不一致，从而可能抛出 `ConcurrentModificationException`。

只读查询适合增强 `for`；需要按位置执行 `set(i, value)` 或 `remove(i)` 时可使用索引循环，也可以显式使用 `Iterator.remove()`。

### 11. 泛型解决了什么问题？什么是类型擦除？

泛型在编译期约束元素类型，减少强制类型转换并提前发现类型错误。例如 `Repository<Student, String>` 明确仓库保存 Student，并以 String 作为编号。

Java 泛型主要通过类型擦除实现：编译后多数泛型类型参数不会作为独立运行时类型存在。因此不能直接 `new T()`，也不能使用 `T.class`。

### 12. `? extends T` 和 `? super T` 有什么区别？

`? extends T` 表示某个 T 的子类型，适合从集合中读取 T，但通常不能安全写入具体对象；`? super T` 表示 T 的某个父类型，适合写入 T，但读取时只能安全当作 Object。

口诀是 PECS：Producer Extends，Consumer Super。只读取学生的统计方法可以接受生产 Student 的集合；向集合中加入 Student 时使用 `? super Student` 更合适。

## 三、异常与 IO

### 13. Java 异常体系的主要结构是什么？

`Throwable` 下主要有 `Error` 和 `Exception`。Error 表示 JVM 或系统层面的严重问题，例如 `OutOfMemoryError`；Exception 表示程序可以处理或向上报告的问题。RuntimeException 是 Exception 的一个重要分支。

业务代码通常处理 Exception，不应该把捕获 Error 当成正常恢复方案。

### 14. 受检异常和非受检异常有什么区别？

受检异常在编译期要求捕获或通过 `throws` 声明，例如 `IOException`。非受检异常继承 `RuntimeException`，编译器不强制处理，通常表示参数错误、状态错误或业务执行失败。

文件 API 抛出 `IOException`；项目把它包装为 `StudentPersistenceException`，让上层看到“学生数据保存失败”这一领域含义。

### 15. `throw`、`throws` 和自定义异常分别是什么？

- `throw` 用于实际抛出一个异常对象。
- `throws` 写在方法声明上，表示方法可能把异常交给调用者。
- 自定义异常用于表达项目自身的失败语义，并可以通过 `super(message, cause)` 保留原始原因。

`throw new StudentPersistenceException("保存失败", e)` 同时提供用户可理解的信息和底层 IOException 原因。

### 16. 什么是 try-with-resources？

实现 `AutoCloseable` 的资源可以写在 `try (...)` 中，代码块结束后 Java 会自动关闭资源，即使过程中发生异常也会执行关闭。

`Scanner`、输入流、输出流和数据库连接都适用。它比手写 finally 更不容易遗漏资源释放。

### 17. 字节流、字符流、字符编码和安全文件写入有什么关系？

字节流处理原始二进制数据；字符流处理文本并涉及编码。UTF-8 规定字符如何转换为字节，读写两端必须使用一致编码。

项目显式使用 `StandardCharsets.UTF_8`。保存时先写同目录临时文件，再替换正式文件；若写入失败则回滚内存，避免直接覆盖造成空文件或半个文件。

## 四、Stream、时间和枚举

### 18. Stream 和普通循环有什么区别？

循环强调“如何一步步执行”；Stream 强调“对数据做什么转换或聚合”。Stream 不负责保存数据，通常只能消费一次，而且中间操作具有惰性，遇到终止操作才真正执行。

增删改查和带 `break` 的控制流程适合循环；平均值、最大值、分组统计适合 Stream。

### 19. `filter`、`map`、`mapToInt`、`collect` 分别做什么？

- `filter` 按条件保留元素。
- `map` 把元素转换为另一种形式。
- `mapToInt` 转为专门的 IntStream，方便求和、平均值和最大值。
- `collect` 把流汇总为集合、Map 或其他结果。

`students.stream().mapToInt(Student::getAge).average()` 用来计算平均年龄；`groupingBy` 和 `counting` 用来按性别计数。

### 20. `LocalDateTime`、`Instant` 和旧的 `Date` 应该如何理解？

`LocalDateTime` 表示不带时区的本地日期时间，适合“2026-07-19 10:00”这类业务时间；`Instant` 表示 UTC 时间线上的一个瞬间，适合跨时区时间戳。现代代码优先使用 `java.time` API。

学生创建时间在本地教学程序中使用 `LocalDateTime`；跨地区服务的审计时间通常更适合 Instant，并在展示时转换时区。

### 21. 为什么使用枚举而不是普通字符串？

枚举把合法值限制在固定集合中，编译期可检查，能用于 switch，也可以拥有字段和方法。字符串容易出现拼写、大小写和非法值问题。

`Gender.MAN` 和 `Gender.WOMAN` 比任意字符串安全；`Gender.fromText()` 负责把用户文本统一转换为合法枚举。

## 五、多线程

### 22. 进程和线程有什么区别？调用 `start()` 和 `run()` 有什么区别？

进程是操作系统资源分配单位，线程是进程中的执行单元。同一进程的线程共享堆等资源，但各自拥有调用栈。

`thread.start()` 请求 JVM 创建新线程，新线程随后执行 `run()`；直接调用 `run()` 只是当前线程的普通方法调用。

### 23. 什么是竞态条件、可见性和原子性？

多个线程在缺少正确同步时共同读写状态，执行结果依赖不可预测的交错顺序，这就是竞态条件。可见性表示一个线程的修改能否被其他线程看到；原子性表示一个操作是否不可被拆分和穿插。

`count++` 包含读取、加一、写回，不是原子操作，两个线程可能丢失更新。

### 24. `synchronized` 和 `AtomicInteger` 如何选择？

`synchronized` 使用监视器锁，可以保护由多步操作组成的临界区，并同时提供互斥和可见性。`AtomicInteger` 使用原子指令完成单个整数的无锁更新，适合简单计数器。

只做 `incrementAndGet()` 可使用 AtomicInteger；需要同时检查多个字段并一起修改时，通常需要锁或其他并发控制。

### 25. 为什么使用线程池而不是不断 `new Thread()`？

线程创建和销毁有成本，数量失控还会耗尽内存与调度资源。线程池复用固定数量的工作线程，并通过任务队列控制执行。

使用 `ExecutorService` 后必须调用 `shutdown()`，并可使用 `awaitTermination()` 等待任务完成；超时后再考虑 `shutdownNow()`。

### 26. `Runnable`、`Callable`、`Future` 和线程中断有什么关系？

Runnable 没有返回值且不能直接声明受检异常；Callable 可以返回结果并抛出异常。提交 Callable 后得到 Future，可用于等待结果、检查完成状态、设置超时或取消任务。

`cancel(true)` 只是发送中断请求。任务必须在阻塞方法抛出 `InterruptedException` 时退出，或主动检查中断状态，不能假设任务一定立刻停止。

## 六、JVM、Maven 与测试

### 27. JVM 运行时内存区域如何理解？

堆主要保存对象和数组，由垃圾收集器管理；每个线程有自己的 Java 虚拟机栈，每次方法调用创建栈帧；方法区的实现保存类元数据等信息。程序计数器记录线程当前执行位置。

大量仍被集合引用的数组会占满堆；无限递归不断创建栈帧，会导致 `StackOverflowError`。

### 28. GC 如何判断对象可以回收？为什么仍会发生 OOM？

主流 JVM 从 GC Roots 出发做可达性分析，无法到达的对象才有资格回收。GC 不是简单地“删除不用的变量”，也不能回收仍被有效引用的对象。

如果 List 一直保存新数组，这些数组仍可达，GC 无法释放，最终会发生 `OutOfMemoryError`。内存泄漏在 Java 中通常表现为“不再需要的对象仍被引用”。

### 29. Maven 的作用、生命周期和依赖 scope 是什么？

Maven 使用 `pom.xml` 管理项目坐标、依赖、插件和标准构建流程。常用阶段包括 `compile`、`test`、`package`、`verify` 和 `install`；执行后面的阶段会先执行前面的阶段。

`scope=test` 表示依赖只用于测试编译和测试运行，不进入正式运行依赖。项目中的 JUnit 就应使用 test scope。

### 30. JUnit 单元测试应该验证什么？如何保持测试独立？

单元测试通常遵循 Arrange、Act、Assert：准备数据，执行行为，断言可观察结果。既要覆盖正常路径，也要覆盖边界值和异常路径。

测试不能依赖执行顺序或共享可变数据。文件测试使用 `@TempDir` 获得独立临时目录；`assertThrows` 验证异常；Repository 测试验证新增、重复、更新、删除和防御性复制。
