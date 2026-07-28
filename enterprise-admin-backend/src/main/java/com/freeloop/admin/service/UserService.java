package com.freeloop.admin.service;

import com.freeloop.admin.entity.User;
import com.freeloop.admin.dto.UserCreateRequest;

public interface UserService {

    User getById(Long id);

    Long createUser(UserCreateRequest request);
}