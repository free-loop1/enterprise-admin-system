package com.freeloop.admin.controller;

import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import com.freeloop.admin.entity.User;
import com.freeloop.admin.service.UserService;
import com.freeloop.admin.vo.UserDetailVO;
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
    public ResponseEntity<UserDetailVO> getUser(@PathVariable long id) {
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
    public String updateUser(@PathVariable long id, @RequestBody UserUpdateRequest request) {
        return "Updated user ID:" + id + ":" + request.getUsername();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> createUser(
            @RequestBody UserCreateRequest request) {
        Long newUserId = userService.createUser(request);
        URI location = URI.create("/api/users/" + newUserId);
        return ResponseEntity
                .created(location)
                .build();
    }
}

