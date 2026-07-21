# 第 1 周：Spring Boot 与 REST API 总结

## 一、本周目标

本周完成了 `enterprise-admin-backend` 的 Spring Boot 项目骨架，并围绕 HTTP 请求与 REST API 学习了 Controller、请求参数、路径参数、JSON 请求体和 HTTP 状态码。

当前技术基线：

- JDK 25
- Maven
- Spring Boot 4.1.0
- Spring Web MVC
- JUnit / Spring Boot Test
- IntelliJ IDEA HTTP Client

本周代码暂时不连接数据库。用户接口只是为了学习 HTTP 和 Spring MVC 注解，不代表用户已经被真正保存。

## 二、Spring Boot 如何启动

项目的启动入口是：

```text
@SpringBootApplication
public class EnterpriseAdminBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseAdminBackendApplication.class, args);
    }
}
```

执行过程可以简单理解为：

1. JVM 执行 `main()` 方法。
2. `SpringApplication.run()` 创建并启动 Spring 容器。
3. Spring 根据自动配置准备 Web MVC、JSON 转换和内嵌 Tomcat。
4. Spring 扫描组件并注册 Controller 等 Bean。
5. Tomcat 监听端口，等待客户端发送 HTTP 请求。

`@SpringBootApplication` 主要组合了以下能力：

- 配置类声明。
- Spring Boot 自动配置。
- 从启动类所在包开始进行组件扫描。

## 三、为什么启动类能够发现 Controller

启动类位于：

```text
com.freeloop.admin
```

Controller 位于：

```text
com.freeloop.admin.controller
```

`controller` 是启动类所在包的子包。Spring 默认从启动类所在包向下扫描，因此能够发现带有 `@RestController` 的类并将其注册到 Spring 容器。

如果把 Controller 放到完全无关的包下，默认组件扫描可能无法发现它。这也是启动类通常放在项目根包中的原因。

## 四、Controller 与请求映射

### 1. `@RestController`

`@RestController` 表示当前类负责接收 HTTP 请求，并将方法返回值直接写入 HTTP 响应体。

当前项目中的 Controller：

- `HelloController`：负责 hello 和 greet 教学接口。
- `UserController`：负责用户资源相关接口。

### 2. `@RequestMapping`

`@RequestMapping` 可以定义一组接口共同的路径前缀。

例如：

```text
@RequestMapping("/api/users")
```

再配合：

```text
@GetMapping("/{id}")
```

最终请求路径是：

```text
/api/users/{id}
```

类级路径与方法级路径会组合，而不是相互替代。

### 3. HTTP 方法映射注解

- `@GetMapping`：查询资源。
- `@PostMapping`：创建资源或提交一次非幂等操作。
- `@PutMapping`：根据确定的资源地址更新资源。
- `@DeleteMapping`：删除资源。

## 五、`@RequestParam` 与 `@PathVariable`

### 1. 查询参数 `@RequestParam`

示例：

```http
GET /api/greet?name=Codex
```

其中 `name` 是查询参数，适合表示搜索条件、筛选条件、排序方式和分页参数。

```text
@RequestParam(value = "name", defaultValue = "Guest") String name
```

配置 `defaultValue` 后，请求没有提供 `name` 时会使用 `Guest`，而不会因为缺少参数返回 `400`。

### 2. 路径参数 `@PathVariable`

示例：

```http
GET /api/users/1001
```

其中 `1001` 表示要访问的用户资源 ID。

```text
@GetMapping("/{id}")
public String getUser(@PathVariable long id)
```

路径参数适合标识具体资源，例如用户 ID、订单 ID 和文章 ID。

### 3. 使用场景对比

```text
查询用户详情：GET /api/users/1001
按用户名搜索：GET /api/users?username=alice
```

前者使用路径参数，后者使用查询参数。

## 六、`@RequestBody` 如何接收 JSON

客户端请求：

```http
POST /api/users
Content-Type: application/json

{
  "username": "Alice"
}
```

Controller 使用：

```text
@RequestBody UserCreateRequest request
```

Spring MVC 会调用 JSON 转换组件，将请求体中的 JSON 转换成 `UserCreateRequest` 对象。

DTO 中的 JavaBean 属性需要正确命名。例如 JSON 字段是 `username`，对应方法应为：

```text
getUsername()
setUsername(...)
```

如果误写成 `getName()` 和 `setName()`，JSON 转换组件会把它理解成 `name` 属性，导致 `username` 没有正确赋值。

`Content-Type: application/json` 用于告诉服务器请求体采用 JSON 格式。JSON 不应该写在 `application-dev.yml` 中，因为 JSON 是一次请求的数据，而 YAML 是应用配置。

## 七、GET、POST、PUT、DELETE 的使用场景

### GET

用于读取资源，不应修改服务器数据。

```http
GET /api/users/1001
```

### POST

通常用于向资源集合提交新资源。

```http
POST /api/users
```

创建时一般不要求客户端提前知道新资源 ID，服务器创建完成后返回新 ID 和资源地址。

### PUT

通常用于更新指定资源。

```http
PUT /api/users/1001
```

### DELETE

用于删除指定资源。

```http
DELETE /api/users/1001
```

GET、PUT、DELETE 通常具有幂等语义，也就是多次执行同一个请求，最终服务器状态应与执行一次相同。POST 通常不保证幂等。

## 八、常见 HTTP 状态码

### `200 OK`

请求成功，并且通常有响应体。本周的查询和模拟修改接口使用了 `200`。

### `201 Created`

新资源创建成功。`POST /api/users` 返回 `201`，比普通的 `200` 更准确。

### `204 No Content`

请求成功，但没有响应体。删除用户接口使用：

```text
ResponseEntity.noContent().build()
```

### `400 Bad Request`

请求内容无法满足接口要求，例如缺少必填参数、JSON 格式错误或请求体无法转换。

### `404 Not Found`

请求路径没有对应资源或接口。后续接入数据库后，查询不存在的用户也应该正确处理为资源不存在。

### `405 Method Not Allowed`

请求路径存在，但使用了不支持的 HTTP 方法。例如接口只支持 PUT，客户端却发送 DELETE。

## 九、`ResponseEntity` 的作用

直接返回 `String` 时，Spring 通常自动返回 `200 OK` 并把字符串写入响应体。

`ResponseEntity<T>` 可以显式控制：

- HTTP 状态码。
- 响应头。
- 响应体。

删除接口：

```text
return ResponseEntity.noContent().build();
```

创建接口：

```text
return ResponseEntity
        .created(location)
        .body("Created user ID:" + newUserId + ":" + request.getUsername());
```

## 十、`Location` 响应头的作用

创建资源成功后，服务器可以通过 `Location` 告诉客户端新资源的访问地址。

本周创建用户接口返回：

```text
HTTP/1.1 201
Location: /api/users/1001
```

`ResponseEntity.created(location)` 会自动设置：

- `201 Created`
- `Location` 响应头

当前 ID `1001` 是教学阶段的模拟值。接入 MySQL 后，应使用数据库实际生成的用户 ID。

## 十一、为什么拆分 Controller

最初所有教学接口都写在 `HelloController` 中。随着用户接口增加，同一个类同时负责问候和用户资源，职责开始混杂。

拆分后：

```text
HelloController
├── GET /api/hello
├── GET /api/greet
└── POST /api/greet

UserController
├── GET /api/users/{id}
├── POST /api/users
├── PUT /api/users/{id}
└── DELETE /api/users/{id}
```

这样可以：

- 让每个 Controller 只负责一种资源或一类业务。
- 降低类的复杂度。
- 方便查找、测试和维护接口。
- 避免项目扩大后出现包含所有接口的“大 Controller”。

重构只改变 Java 代码组织，没有改变外部接口地址，这说明接口契约可以与内部代码结构分离。

## 十二、本周错误、原因与修复

### 1. PowerShell 中 curl JSON 请求返回 `400`

JSON 本身可以是合法的，但 PowerShell 向原生命令传递引号时可能改变参数内容，导致服务端收到的请求体不是预期 JSON。

后续人工接口测试统一使用 IntelliJ IDEA HTTP Client 的 `.http` 文件，避免不同命令行环境的转义差异。

### 2. `username` 得到 `null`

`UserUpdateRequest` 的字段是 `username`，但 getter/setter 曾使用 `getName()` 和 `setName()`，不符合对应的 JavaBean 属性命名。

修复为 `getUsername()` 和 `setUsername()` 后，JSON 字段才能正确映射。

### 3. 重复 `@DeleteMapping` 导致启动失败

同一个 Controller 中曾存在两个完全相同的：

```text
DELETE /api/users/{id}
```

Spring 无法判断应该调用哪个方法，因此报告 `Ambiguous mapping` 并阻止应用启动。

修复方法是删除旧方法，只保留一个删除接口。这个错误说明方法名不同不能区分 HTTP 接口，真正用于映射的是 HTTP 方法和路径。

### 4. `/user` 与 `/users` 不一致导致请求失败

接口定义和测试地址的单复数不一致时，请求无法命中预期映射，可能出现 `404` 或 `405`。

项目统一使用复数资源路径：

```text
/api/users
```

### 5. HTTP `Date` 显示 GMT

响应头：

```text
Date: Tue, 21 Jul 2026 07:57:26 GMT
```

HTTP `Date` 按标准使用 GMT/UTC。中国标准时间为 UTC+8，因此 `07:57:26 GMT` 对应北京时间 `15:57:26`，两者表示同一时刻，不是服务器时间错误。


## 十三、HTTP Client 文件

人工请求按 Controller 拆分：

```text
http/hello-controller.http
http/user-controller.http
```

`hello-controller.http` 验证：

- 基础 GET 请求。
- `@RequestParam`。
- POST JSON 与 `@RequestBody`。

`user-controller.http` 验证：

- `@PathVariable` 查询用户。
- PUT JSON 修改用户。
- DELETE 返回 `204`。
- POST 返回 `201` 和 `Location`。

## 十四、本周验收结果

### 已完成

- Spring Boot 4.1.0 项目可以启动。
- `dev` Profile 正常激活。
- Tomcat 在 `8080` 端口启动。
- Hello、查询参数、路径参数、POST、PUT、DELETE 教学接口已实现。
- 创建用户接口返回 `201 Created`。
- 创建用户响应包含 `Location: /api/users/1001`。
- 删除用户接口返回 `204 No Content`。
- `HelloController` 与 `UserController` 已按职责拆分。
- HTTP Client 请求已按 Controller 拆分。
- Spring 上下文自动化测试通过。
- Maven 验收结果：`1` 个测试，`0 failures`、`0 errors`，`BUILD SUCCESS`。

### 当前限制

- 用户数据没有保存到数据库。
- 用户 ID 仍是硬编码的 `1001`。
- Controller 仍直接返回教学字符串。
- 尚未引入 Entity、Mapper 和 Service。
- 尚未加入参数校验、统一返回和全局异常处理。
- 当前自动化测试只验证 Spring 上下文启动，还没有逐个验证 Controller 响应。

## 十五、下一步

第 2 周进入 MySQL 与 MyBatis-Plus：

1. 检查 MySQL 服务和连接状态。
2. 创建 `enterprise_admin` 数据库，避免与旧的 `enterprise_admin_system` 混淆。
3. 分析用户业务字段。
4. 设计 `sys_user` 表、约束和索引。
5. 将建库建表 SQL 保存到项目 `sql/` 目录。
6. 接入 MySQL 驱动和 MyBatis-Plus Boot 4 Starter。
7. 逐步创建 Entity、Mapper、Service 和 Controller，不一次性生成全部代码。

