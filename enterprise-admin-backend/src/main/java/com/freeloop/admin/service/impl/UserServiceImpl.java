package com.freeloop.admin.service.impl;

import com.freeloop.admin.entity.User;
import com.freeloop.admin.mapper.UserMapper;
import com.freeloop.admin.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}