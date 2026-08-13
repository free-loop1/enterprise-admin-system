package com.freeloop.admin.service.impl;

import com.freeloop.admin.dto.RegisterRequest;
import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.service.AuthService;
import com.freeloop.admin.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public Long register(RegisterRequest request) {
        UserCreateRequest createRequest =
                new UserCreateRequest();

        createRequest.setUsername(request.getUsername());
        createRequest.setPassword(request.getPassword());
        createRequest.setNickname(request.getNickname());
        createRequest.setPhone(request.getPhone());
        createRequest.setEmail(request.getEmail());

        createRequest.setStatus(1);

        return userService.createUser(createRequest);
    }
}