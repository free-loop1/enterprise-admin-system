# 第 2 周：学生管理系统 v2

## 本周目标

使用学生管理系统练习封装、继承、多态、接口、抽象类、`ArrayList`、`HashMap`和泛型，并把界面交互与数据存储分离。

## 已完成功能

- 添加学生，并拒绝重复学号
- 查看全部学生
- 根据学号查询学生
- 修改学生信息
- 根据学号删除学生
- 校验空学号、空姓名、年龄范围、性别和空手机号
- 支持`ArrayList`和`HashMap`两种仓库实现

## 当前结构

```text
StudentManagerApp
    |
    v
Repository<Student, String>
    |-- ArrayListStudentRepository
    `-- HashMapStudentRepository

Person（抽象类）
    `-- Student
```

各类职责：

- `Person`：保存人员共有属性并声明`getRole()`抽象行为。
- `Student`：继承`Person`，保存不可变学号并实现学生角色。
- `Repository<T, ID>`：用泛型定义增删改查契约。
- `ArrayListStudentRepository`：使用顺序集合实现仓库。
- `HashMapStudentRepository`：使用学号作为键实现快速查询。
- `StudentManagerApp`：只负责读取输入、调用仓库和展示结果。

## 学到了什么

### 封装

属性使用`private`隐藏，构造方法和setter集中校验数据。学号会作为`HashMap`的键，因此使用`final`保持创建后不可变。

### 继承与抽象类

`Student extends Person`复用姓名、年龄、性别和手机号。`Person`不能直接实例化，并要求具体子类实现`getRole()`。

### 多态

`printPerson(Person person)`可以接收`Student`，运行时调用`Student.getRole()`和`Student.toString()`。

App依赖接口类型：

```java
Repository<Student, String> repository = new HashMapStudentRepository();
```

只替换右侧实现，就能从`ArrayList`切换到`HashMap`。

### 接口与泛型

`Repository<T, ID>`中的`T`表示实体类型，`ID`表示编号类型。`Repository<Student, String>`表示仓库保存学生，并使用字符串学号查询。

### ArrayList

- 保持插入顺序。
- 按学号查询、更新和删除需要遍历，平均复杂度为`O(n)`。
- 查询适合增强`for`；需要`set(i, ...)`或`remove(i)`时使用索引循环。

### HashMap

- 使用`学号 -> Student`保存键值对。
- 按学号查询、更新和删除的平均复杂度接近`O(1)`。
- `HashMap`不保证遍历顺序。

## 代码审查与重构

| 审查发现 | 风险 | 重构结果 |
| --- | --- | --- |
| 学号可以通过setter修改 | HashMap的键与Student内部学号可能不一致 | 学号改为`final`并删除setter |
| `main()`包含全部菜单与业务流程 | 方法过长，难以阅读和测试 | 拆分为添加、列表、查询、更新、删除和输入方法 |
| 父类构造器调用可重写setter | 子类尚未初始化时可能执行子类逻辑 | 构造器改用私有静态校验方法直接赋值 |
| App内部直接操作集合 | 更换存储方式需要重写菜单 | App改为依赖`Repository<Student, String>` |
| 仓库可以接收`null`实体 | 更新时可能发生空指针异常 | `add()`和`update()`增加非空检查 |
| 变量命名和格式不统一 | 阅读成本增加 | 使用`scanner`、`student`、`id`等驼峰命名并统一空格 |
| HashMap添加需要先判断再写入 | 容易写错成功返回值 | 使用`putIfAbsent()`同时表达去重和添加 |

## 踩过的坑与解决方法

### 1. 固定数组容量不足

问题：`Student[10]`最多保存10名学生，还要手动维护`size`和删除后的元素移动。

解决：使用`ArrayList<Student>`动态保存学生。

### 2. 直接打印对象得到地址

问题：输出类似`Student@6d6f6e28`。

解决：在`Student`中重写`toString()`。

### 3. 增强for中直接删除元素

问题：增强`for`底层使用迭代器，直接修改集合结构可能触发`ConcurrentModificationException`。

解决：删除时使用索引循环和`remove(i)`；只读查询使用增强`for`。

### 4. 把学号传给ArrayList.set()

问题：`set()`需要整数下标，不能传字符串学号。

解决：使用索引循环找到位置，再执行`students.set(i, entity)`。

### 5. 仓库中读取Scanner

问题：仓库同时负责输入和数据存储，职责混乱，难以复用。

解决：`StudentManagerApp`负责输入输出，仓库只接收已经创建好的`Student`。

### 6. 使用null判断学生列表

问题：`findAll()`返回的是空集合而不是`null`。

解决：使用`students.isEmpty()`判断是否没有数据。

### 7. 使用update()判断学生是否存在

问题：`repository.update(repository.findById(id))`可能把`null`传入更新方法，也没有产生新数据。

解决：先调用`findById()`判断，再读取新信息、创建新对象，最后调用一次`update()`。

### 8. HashMap添加成功却返回false

问题：已经执行`put()`，但返回值仍写成`false`，导致界面显示添加失败。

解决：成功写入后返回`true`，重构后使用`putIfAbsent()`简化逻辑。

### 9. Git显示AM

问题：文件已经暂存，之后又继续修改，暂存区仍是旧版本。

解决：最终提交前重新执行`git add`，再用`git diff --cached`检查真正要提交的内容。

### 10. 自动测试中文乱码

问题：PowerShell向Java传递中文时编码不一致，控制台测试输出乱码。

解决：源码统一使用UTF-8编译；区分终端显示编码问题和Java业务逻辑问题。

### 11. 构造器调用可重写方法

问题：父类构造器调用非`final`的setter时，子类可能重写该方法，导致对象尚未初始化完成就执行子类逻辑。`javac -Xlint:all`会提示`this-escape`。

解决：将校验提取为私有静态方法；构造器直接完成校验和赋值，setter复用同一套校验方法。

## 阶段验收

- [x] 项目能够编译
- [x] 封装和数据校验有效
- [x] 抽象类、继承和多态已应用
- [x] 泛型仓库接口已实现
- [x] ArrayList仓库支持完整增删改查
- [x] HashMap仓库支持完整增删改查
- [x] App可以在不改菜单逻辑的情况下切换仓库实现
- [x] 重复学号和不存在学号能够正确处理

## 后续可以改进

- 使用JUnit编写自动化测试
- 增加业务服务层，进一步分离界面与业务规则
- 按`model`、`repository`、`service`、`app`拆分包
- 将内存数据替换为MySQL持久化
- 对输入错误增加重试，而不是直接返回主菜单

## 本周Git提交流程

```powershell
git status
git add notes/week02-student-manager-v2
git diff --cached
git commit -m "feat: complete student manager v2"
git pull --rebase origin main
git push
git status
```
