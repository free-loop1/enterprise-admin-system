package com.freeloop.admin.service;

import com.freeloop.admin.dto.RegisterRequest;

public interface AuthService {

    Long register(RegisterRequest request);
}