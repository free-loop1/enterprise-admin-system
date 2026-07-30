package com.freeloop.admin.controller;

import com.freeloop.admin.common.Result;
import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import com.freeloop.admin.service.UserService;
import com.freeloop.admin.vo.PageResult;
import com.freeloop.admin.vo.UserDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(
        name = "用户管理",
        description = "用户新增、查询、修改、删除和分页查询接口"
)
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "查询用户详情",
            description = "根据用户 ID 查询未被逻辑删除的用户"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Result<UserDetailVO>> getUser(
            @Parameter(
                    description = "用户 ID",
                    example = "1",
                    required = true
            )
            @PathVariable
            @Positive(message = "用户 ID 必须大于 0") long id) {
        UserDetailVO user = userService.getById(id);
        Result<UserDetailVO> result = Result.success(user);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "修改用户",
            description = "根据用户 ID 更新请求中提供的用户字段"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Result<Void>> updateUser(
            @Parameter(
                    description = "用户 ID",
                    example = "1",
                    required = true
            )
            @PathVariable
            @Positive(message = "用户 ID 必须大于 0") Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(Result.success());
    }

    @Operation(
            summary = "删除用户",
            description = "根据用户 ID 对用户进行逻辑删除"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteUser(
            @Parameter(
                    description = "用户 ID",
                    example = "1",
                    required = true
            )
            @PathVariable
            @Positive(message = "用户 ID 必须大于 0") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Result.success());
    }

    @Operation(
            summary = "创建用户",
            description = "创建新用户，成功后返回新用户 ID"
    )
    @PostMapping
    public ResponseEntity<Result<Long>> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        Long newUserId = userService.createUser(request);
        URI location = URI.create("/api/users/" + newUserId);
        return ResponseEntity
                .created(location)
                .body(Result.success(newUserId));
    }

    @Operation(
            summary = "分页查询用户",
            description = "分页查询未被逻辑删除的用户，可按用户名模糊搜索"
    )
    @GetMapping
    public ResponseEntity<Result<PageResult<UserDetailVO>>> pageUsers(
            @Parameter(
                    description = "页码，从 1 开始",
                    example = "1"
            )
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "页码必须大于等于 1") long page,

            @Parameter(
                    description = "每页数量，范围为 1～100",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于 1")
            @Max(value = 100, message = "每页数量不能超过 100") long size,

            @Parameter(
                    description = "用户名模糊搜索关键词",
                    example = "alice"
            )
            @RequestParam(required = false)
            @Size(max = 50, message = "搜索用户名长度不能超过 50 个字符")
            String username) {

        PageResult<UserDetailVO> result =
                userService.pageUsers(page, size, username);

        return ResponseEntity.ok(Result.success(result));
    }
}

