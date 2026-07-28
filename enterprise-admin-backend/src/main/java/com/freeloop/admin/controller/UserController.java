package com.freeloop.admin.controller;

import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import com.freeloop.admin.entity.User;
import com.freeloop.admin.service.UserService;
import com.freeloop.admin.vo.PageResult;
import com.freeloop.admin.vo.UserDetailVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailVO> getUser(
            @PathVariable
            @Positive(message = "用户 ID 必须大于 0") long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserDetailVO userDetailVO = new UserDetailVO();
        userDetailVO.setId(user.getId());
        userDetailVO.setUsername(user.getUsername());
        userDetailVO.setNickname(user.getNickname());
        userDetailVO.setPhone(user.getPhone());
        userDetailVO.setEmail(user.getEmail());
        userDetailVO.setStatus(user.getStatus());
        return ResponseEntity.ok(userDetailVO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable
            @Positive(message = "用户 ID 必须大于 0") Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        boolean updated = userService.updateUser(id, request);

        if (!updated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable
            @Positive(message = "用户 ID 必须大于 0") Long id) {
        boolean deleted = userService.deleteUser(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        Long newUserId = userService.createUser(request);
        URI location = URI.create("/api/users/" + newUserId);
        return ResponseEntity
                .created(location)
                .build();
    }

    @GetMapping
    public ResponseEntity<PageResult<UserDetailVO>> pageUsers(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "页码必须大于等于 1") long page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于 1")
            @Max(value = 100, message = "每页数量不能超过 100") long size,

            @RequestParam(required = false)
            @Size(max = 50, message = "搜索用户名长度不能超过 50 个字符")
            String username) {

        PageResult<UserDetailVO> result =
                userService.pageUsers(page, size, username);

        return ResponseEntity.ok(result);
    }
}

