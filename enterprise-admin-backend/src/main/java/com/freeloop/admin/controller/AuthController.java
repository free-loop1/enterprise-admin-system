package com.freeloop.admin.controller;

import com.freeloop.admin.common.Result;
import com.freeloop.admin.dto.RegisterRequest;
import com.freeloop.admin.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Result<Long>> register(
            @Valid @RequestBody RegisterRequest request) {

        Long userId = authService.register(request);

        return ResponseEntity
                .created(URI.create("/api/users/" + userId))
                .body(Result.success(userId));
    }
}