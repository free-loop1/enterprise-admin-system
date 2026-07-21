package com.freeloop.admin.controller;

import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/{id}")
    public String getUser(@PathVariable long id) {
        return "User ID:" + id;
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
    public ResponseEntity<String> createUser(
            @RequestBody UserCreateRequest request) {
        long newUserId = 1001L;
        URI location = URI.create("/api/users/" + newUserId);
        return ResponseEntity
                .created(location)
                .body("Created user ID:" + newUserId + ":" + request.getUsername());
    }
}

