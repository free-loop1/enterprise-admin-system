package com.freeloop.admin.service.impl;

import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import com.freeloop.admin.entity.User;
import com.freeloop.admin.mapper.UserMapper;
import com.freeloop.admin.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Long createUser(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());

        userMapper.insert(user);

        return user.getId();
    }

    @Override
    public boolean updateUser(Long id, UserUpdateRequest request) {
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            return false;
        }
        User user = new User();
        user.setId(id);
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        return userMapper.updateById(user) == 1;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) == 1;
    }
}