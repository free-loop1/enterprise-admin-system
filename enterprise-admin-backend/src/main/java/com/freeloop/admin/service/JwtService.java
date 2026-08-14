package com.freeloop.admin.service;

import io.jsonwebtoken.Claims;

public interface JwtService {

    String generateAccessToken(
            Long userId,
            String username,
            Integer tokenVersion
    );

    Claims parseAccessToken(String token);
}