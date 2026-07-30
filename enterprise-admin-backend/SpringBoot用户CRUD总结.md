# Spring Boot 用户 CRUD 阶段总结

## 一、阶段目标

本阶段在独立模块 `enterprise-admin-backend` 中完成企业权限管理系统 v1 用户模块，主要目标包括：

- 掌握 Spring Boot 项目结构和配置文件。
- 使用 Spring MVC 编写 RESTful API。
- 使用 MySQL 和 MyBatis-Plus 完成用户 CRUD。
- 区分 Entity、DTO 和 VO。
- 使用 Bean Validation 校验请求参数。
- 实现统一响应、业务错误码和全局异常处理。
- 使用 SpringDoc 和 Swagger UI 生成接口文档。
- 使用 JUnit、Mockito、MockMvc 和 HTTP Client 验证接口。
- 学习主键、唯一索引、联合索引和 `EXPLAIN` 基础。

## 二、技术栈

| 技术 | 当前用途 |
|---|---|
| JDK 25 | Java 运行和编译环境 |
| Spring Boot 4.1.0 | 应用启动、自动配置和依赖管理 |
| Spring MVC | REST API 和请求参数绑定 |
| MySQL 9.7 | 用户数据持久化 |
| MyBatis-Plus 3.5.17 | CRUD、条件构造器、分页和逻辑删除 |
| Bean Validation | DTO、路径参数和查询参数校验 |
| Spring Security Crypto | 密码哈希，不包含登录认证功能 |
| SpringDoc 3.0.3 | OpenAPI 文档和 Swagger UI |
| JUnit、Mockito、MockMvc | Service 和 Controller 自动化测试 |
| IntelliJ IDEA HTTP Client | 接口正常与异常场景验收 |

Spring Boot 父项目统一管理常用依赖和 Maven 插件版本。MyBatis-Plus 和 SpringDoc 不属于 Spring Boot 默认依赖管理范围，因此在 `pom.xml` 中通过属性统一维护版本。

## 三、模块结构与分层职责

主要调用链如下：

```text
HTTP 请求
    ↓
Controller：接收参数、校验、设置 HTTP 状态
    ↓
Service：业务规则、事务、Entity 与 VO 转换
    ↓
Mapper：执行 MyBatis-Plus 数据访问
    ↓
MySQL：sys_user
```

主要包职责：

| 包 | 职责 |
|---|---|
| `controller` | REST API 和 HTTP 响应 |
| `service` | 业务接口 |
| `service.impl` | 业务实现和事务边界 |
| `mapper` | MyBatis-Plus 数据访问 |
| `entity` | 数据库表映射对象 |
| `dto` | 接收客户端请求数据 |
| `vo` | 返回客户端的公开数据 |
| `common` | 统一响应和错误码 |
| `exception` | 业务异常和全局异常处理 |
| `config` | MyBatis-Plus、密码编码和 OpenAPI 配置 |

### Entity、DTO、VO 的区别

- `User` 是 Entity，与 `sys_user` 表结构对应，包含密码哈希、逻辑删除标记和时间字段。
- `UserCreateRequest`、`UserUpdateRequest` 是 DTO，只接收客户端允许提交的字段。
- `UserDetailVO` 是 VO，只包含允许公开返回的字段，不包含密码和逻辑删除标记。
- `PageResult<T>` 是分页 VO，统一返回记录、总数、页码、每页数量和总页数。

当前详情和分页查询在 SQL 字段选择阶段就排除了 `password` 和 `deleted`，而不是查询完整 Entity 后只依赖 JSON 序列化隐藏敏感字段。

## 四、数据库与 sys_user 表

阶段 2 使用数据库：

```text
enterprise_admin
```

不要与阶段准备时使用的 `enterprise_admin_system` 混淆。

`sys_user` 主要字段：

| 字段 | 含义 |
|---|---|
| `id` | 自增用户主键 |
| `username` | 登录用户名 |
| `password` | 密码哈希 |
| `nickname` | 用户昵称 |
| `phone` | 手机号 |
| `email` | 邮箱 |
| `status` | `0` 禁用，`1` 正常 |
| `created_at` | 创建时间 |
| `updated_at` | 更新时间 |
| `deleted` | `0` 未删除，`1` 已逻辑删除 |

数据库使用 `utf8mb4` 字符集和 `utf8mb4_0900_ai_ci` 排序规则。数据库层还通过 `CHECK` 约束限制 `status` 和 `deleted` 只能为 `0` 或 `1`。

### 逻辑删除

`User.deleted` 使用 `@TableLogic`。调用 `deleteById()` 时，MyBatis-Plus 将物理删除转换为逻辑删除；后续普通查询会自动排除 `deleted = 1` 的记录。

当前用户名唯一索引仍会保留逻辑删除用户占用的用户名，因此删除后不能重新注册同名用户。是否允许重用用户名属于后续业务规则，不应只在 Java 代码中随意修改。

## 五、索引与 EXPLAIN 结论

### 主键索引

```sql
PRIMARY KEY (id)
```

用途：

- 唯一标识用户。
- 支持详情、修改和删除按 ID 定位。
- InnoDB 使用主键组织表数据。

主键查询的执行计划：

```text
type = const
key = PRIMARY
rows = 1
```

这表示 MySQL 通过主键等值查询，预计只检查一行。

### 用户名唯一索引

```sql
UNIQUE KEY uk_sys_user_username (username)
```

用途：

- 在数据库层最终保证用户名不重复。
- 支持用户名精确查询和重复检查。
- 支持没有前导 `%` 的用户名前缀查询。

用户名精确查询的执行计划为 `type = const`，实际使用 `uk_sys_user_username`。

`username` 是 `VARCHAR(50)`，使用 `utf8mb4` 时每个字符最多占 4 字节，再加 2 字节长度信息，因此执行计划中可看到 `key_len = 202`。

### 包含式与前缀模糊查询

包含式查询：

```sql
username LIKE '%ali%'
```

由于关键词前面存在 `%`，MySQL 无法从用户名索引起点定位。实际执行计划显示：

```text
possible_keys = NULL
type = index
key = PRIMARY
Extra = Using where; Backward index scan
```

这里扫描主键是为了满足 `ORDER BY id DESC`，不代表用户名条件使用了主键。

前缀查询：

```sql
username LIKE 'ali%'
```

执行计划显示：

```text
type = range
key = uk_sys_user_username
Extra = Using index condition; Using where
```

前缀查询可以利用 B+Tree 索引进行范围定位。

### 联合索引与最左匹配

联合索引示例：

```sql
CREATE INDEX idx_sys_user_status_created_at
    ON sys_user (status, created_at);
```

该示例只用于理解，不在当前数据库中执行。索引顺序是 `status → created_at`：

- `WHERE status = 1` 可以从最左列开始使用。
- `WHERE status = 1 AND created_at >= ...` 可以继续使用第二列。
- 只查询 `created_at` 而缺少最左侧 `status`，通常不能有效利用该索引。

当前不新增联合索引，原因是现有接口没有稳定的多字段组合过滤需求，`status` 和 `deleted` 区分度较低，而且额外索引会增加写入和存储成本。`(username, id)` 也通常是冗余索引，因为用户名已经唯一，并且 InnoDB 二级索引会保存主键值。

详细 SQL 保存在：

```text
sql/04-explain-sys-user.sql
```

## 六、用户 REST API

基础路径：

```text
/api/users
```

| 方法 | 路径 | 功能 | 成功状态 |
|---|---|---|---|
| `POST` | `/api/users` | 创建用户 | `201 Created` |
| `GET` | `/api/users/{id}` | 查询用户详情 | `200 OK` |
| `PUT` | `/api/users/{id}` | 部分字段更新 | `200 OK` |
| `DELETE` | `/api/users/{id}` | 逻辑删除用户 | `200 OK` |
| `GET` | `/api/users` | 分页和用户名模糊查询 | `200 OK` |

创建成功后通过 `Location` 响应头返回新资源路径：

```text
Location: /api/users/{newUserId}
```

修改和删除使用 `200 OK` 而不是 `204 No Content`，因为系统要求返回统一的 `Result<Void>` JSON 响应体。

分页接口限制：

- `page` 从 `1` 开始。
- `size` 范围为 `1～100`。
- `username` 最大长度为 50 个字符。
- MyBatis-Plus 分页插件再次限制最大每页数量为 100。

## 七、参数校验

### 创建用户

- 用户名不能为空，长度为 2～50。
- 用户名必须以字母开头，只能包含字母、数字和下划线。
- 密码长度为 8～64，必须包含字母、数字和特殊字符，不能包含空白字符。
- 昵称不能为空，最大长度为 50。
- 手机号符合中国大陆 11 位手机号格式。
- 邮箱最大长度为 100，并符合邮箱格式。
- 状态只能为 `0` 或 `1`。

### 修改用户

- 所有字段都可以不单独必填。
- 整个请求至少提供一个要修改的字段。
- 提供字段后仍需要满足对应格式校验。
- 没有提供的字段不会被更新为 `null`。

Controller 使用 `@Valid` 校验 JSON DTO，并对路径参数和分页查询参数使用 `@Positive`、`@Min`、`@Max`、`@Size` 等约束。

## 八、密码与并发安全

密码使用 `DelegatingPasswordEncoder` 编码后写入数据库，当前默认编码方式为 BCrypt。数据库只保存密码哈希，不保存明文密码，响应 VO 也不包含密码字段。

创建用户采用两层重复用户名保护：

1. Service 预检查，用于尽早返回友好的业务错误。
2. 数据库唯一索引作为并发条件下的最终保证。

即使两个并发请求同时通过预检查，其中一个请求仍会被数据库唯一索引拒绝；Service 捕获 `DuplicateKeyException` 后转换为统一的“用户名已存在”业务异常。

创建完成后还会检查影响行数和数据库生成的主键，防止错误返回 `201 Created` 和 `/api/users/null`。

## 九、事务处理

`UserServiceImpl` 类默认使用：

```java
//@Transactional(readOnly = true)
```

查询方法使用只读事务语义，创建、修改和删除方法单独使用 `@Transactional` 开启可写事务。

当前每个 CRUD 方法只有一条核心写 SQL，但明确的 Service 事务边界有利于以后加入角色关联、审计日志等多表操作。`BusinessException` 和其他运行时异常会触发事务回滚。

## 十、统一响应和错误码

统一响应结构：

```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

`Result<T>` 字段：

- `code`：业务响应码，`0` 表示成功。
- `message`：面向调用方的响应信息。
- `data`：实际响应数据，失败或无返回数据时为 `null`。

当前主要错误码：

| 业务码 | HTTP 状态 | 含义 |
|---:|---:|---|
| `0` | 根据成功场景确定 | 操作成功 |
| `40001` | `400` | 参数校验失败 |
| `40002` | `400` | 请求体格式错误 |
| `40003` | `400` | 参数类型错误 |
| `40400` | `404` | 请求资源不存在 |
| `40401` | `404` | 用户不存在 |
| `40500` | `405` | 请求方法不支持 |
| `40901` | `409` | 用户名已存在 |
| `50000` | `500` | 系统内部错误 |

## 十一、全局异常处理

`GlobalExceptionHandler` 使用 `@RestControllerAdvice` 统一处理：

- `BusinessException`
- DTO 校验异常
- Controller 方法参数校验异常
- JSON 不可读取异常
- 参数类型转换异常
- 请求资源不存在异常
- 请求方法不支持异常
- 未处理的系统异常

参数和业务错误会返回对应业务码；未知系统异常在服务端记录完整日志，但只向客户端返回通用的 `50000`，避免泄露堆栈、SQL 和服务器内部信息。

## 十二、接口文档

项目通过 SpringDoc 生成 OpenAPI 文档：

```text
http://localhost:8080/v3/api-docs
http://localhost:8080/swagger-ui.html
```

用户接口通过 `@Tag`、`@Operation`、`@Parameter` 和 `@Schema` 添加说明。教学使用的 `HelloController` 通过 `@Hidden` 从正式接口文档中隐藏。

公共配置不再自动激活开发环境。开发启动时需要在 IDEA 的 Spring Boot 运行配置中设置：

```text
有效配置文件：dev
环境变量：DB_PASSWORD=本机 MySQL 密码
```

生产配置通过环境变量读取数据库地址、用户名和密码，并关闭 `/v3/api-docs` 与 Swagger UI。

## 十三、测试与验收

### 自动化测试

当前测试组成：

| 测试类 | 数量 | 内容 |
|---|---:|---|
| `UserServiceImplTest` | 11 | 详情、创建、密码编码、重复用户名、更新、删除、分页、字段投影和主键回填异常 |
| `UserControllerTest` | 8 | 统一详情、404、参数校验、创建、分页、更新和删除响应 |
| `DatabaseConnectionIT` | 2 | 数据源连接和真实数据库 Service 查询 |
| `EnterpriseAdminBackendApplicationTests` | 1 | Spring ApplicationContext 启动 |

最新 Maven 快速回归结果：

```text
Tests run: 19
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

这 19 项包含 Service 和 Controller 测试，不依赖真实数据库。最新分层和配置重构后，已在配置 `dev` 与 `DB_PASSWORD` 的 IDEA 环境中重新运行 2 项数据库集成测试和 1 项上下文测试，3 项全部通过。

本阶段自动化测试累计验收结果：

```text
Service 与 Controller 测试：19 项通过
数据库集成测试：2 项通过
ApplicationContext 测试：1 项通过
合计：22 项通过
```

### HTTP Client 验收

`http/user-controller.http` 当前包含 45 个用户接口用例，覆盖：

- CRUD 和分页正常流程。
- 不存在或已删除的用户。
- 创建、修改、删除后的数据验证。
- 用户名、密码、昵称、手机、邮箱和状态校验。
- 路径参数、分页参数和 JSON 类型错误。
- 重复用户名冲突。
- 不存在的请求路径。
- 不支持的 HTTP 方法。

### 最终运行验收

使用 `dev` Profile 启动应用后，最终只读验收结果：

| 验收地址 | 结果 |
|---|---|
| `/v3/api-docs` | `200`，标题为“企业权限管理系统 API”，版本为 `v1` |
| `/swagger-ui.html` | `200`，正常跳转到 `/swagger-ui/index.html` |
| `/api/users/1` | `200`，业务码为 `0`，不包含 `password`、`deleted` |
| `/api/users?page=1&size=1` | `200`，分页结构正确，不包含敏感字段 |

OpenAPI 文档中的业务路径为 `/api/users` 和 `/api/users/{id}`，教学用 `HelloController` 未出现在正式文档中。

## 十四、代码审查结论

已经完成的改进：

- Controller 不再接收 `User` Entity，详情由 Service 返回 VO。
- 查询阶段排除密码和逻辑删除字段。
- 写操作增加事务边界。
- 创建结果检查影响行数和生成主键。
- 数据库密码使用环境变量，不写入仓库。
- 生产环境关闭接口文档。
- 分页数量受到 Controller 和 MyBatis-Plus 双重限制。

仍需保留的技术债：

1. 当前只有密码编码功能，没有登录认证、RBAC 和接口权限校验，不能直接作为生产管理系统上线。
2. 包含式查询 `LIKE '%keyword%'` 会扫描较多记录；数据量增大后需要改为前缀查询或引入专门搜索方案。
3. 逻辑删除后用户名是否允许重用，需要明确业务规则并重新设计数据库约束。
4. Mockito 在 JDK 25 下仍有动态加载 Java Agent 的非阻塞警告，后续需要按构建工具统一配置 Agent。
5. 数据库建表脚本目前由人工执行；进入工程化部署阶段后应学习 Flyway 或 Liquibase。

## 十五、阶段验收清单

| 验收标准 | 状态 |
|---|---|
| 每个用户接口具有正常与异常 HTTP 请求 | 已完成 |
| Maven Service 与 Controller 自动化测试 | 已完成，19 项通过 |
| 最新数据库和上下文测试 | 已完成，3 项通过 |
| 主键、唯一索引、联合索引和 EXPLAIN 基础 | 已完成 |
| 能说明 `sys_user` 索引理由 | 已完成 |
| 用户 CRUD 和分页查询 | 已完成 |
| 统一返回格式 | 已完成 |
| 参数、业务和系统异常统一处理 | 已完成 |
| Swagger/OpenAPI 可访问 | 已完成最终运行验收 |
| 企业权限管理系统 v1 用户模块 | 已完成 |
| `SpringBoot用户CRUD总结.md` | 已完成 |

## 十六、下一步

阶段运行验收已经完成，剩余收尾顺序：

1. 使用 IDEA 检查 Git 状态和本阶段文件范围。
2. 暂存文件并检查暂存区差异。
3. 提交并推送阶段 2 用户模块。
4. 推送后再次检查本地与远程分支状态。

后续安全阶段需要实现登录认证、用户身份识别、角色权限、接口授权和操作审计，解决当前匿名访问与越权风险。
