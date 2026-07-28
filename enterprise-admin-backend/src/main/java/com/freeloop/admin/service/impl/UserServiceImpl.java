package com.freeloop.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import com.freeloop.admin.entity.User;
import com.freeloop.admin.mapper.UserMapper;
import com.freeloop.admin.service.UserService;
import com.freeloop.admin.vo.PageResult;
import com.freeloop.admin.vo.UserDetailVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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

    @Override
    public PageResult<UserDetailVO> pageUsers(long page, long size, String username) {
        Page<User> userPage = new Page<>(page, size);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(StringUtils.hasText(username), User::getUsername, username)
                .orderByDesc(User::getId);

        Page<User> resultPage = userMapper.selectPage(userPage, queryWrapper);

        List<UserDetailVO> records = resultPage.getRecords()
                .stream()
                .map(this::toUserDetailVO)
                .toList();

        PageResult<UserDetailVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(resultPage.getTotal());
        result.setPage(resultPage.getCurrent());
        result.setSize(resultPage.getSize());
        result.setPages(resultPage.getPages());

        return result;
    }

    private UserDetailVO toUserDetailVO(User user) {
        UserDetailVO vo = new UserDetailVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
