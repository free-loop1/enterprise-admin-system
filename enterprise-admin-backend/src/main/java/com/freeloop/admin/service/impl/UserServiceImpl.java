package com.freeloop.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freeloop.admin.common.ResultCode;
import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import com.freeloop.admin.entity.User;
import com.freeloop.admin.exception.BusinessException;
import com.freeloop.admin.mapper.UserMapper;
import com.freeloop.admin.service.UserService;
import com.freeloop.admin.vo.PageResult;
import com.freeloop.admin.vo.UserDetailVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetailVO getById(Long id) {
        LambdaQueryWrapper<User> queryWrapper =
                createUserDetailQueryWrapper()
                        .eq(User::getId, id);

        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(
                    ResultCode.USER_NOT_FOUND
            );
        }

        return toUserDetailVO(user);
    }

    @Override
    @Transactional
    public Long createUser(UserCreateRequest request) {
        validateUniqueFields(
                request.getUsername(),
                request.getPhone(),
                request.getEmail(),
                null
        );

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());

        int affectedRows;

        try {
            affectedRows = userMapper.insert(user);
        } catch (DuplicateKeyException exception) {
            throw convertDuplicateKeyException(exception);
        }

        if (affectedRows != 1 || user.getId() == null) {
            throw new IllegalStateException(
                    "创建用户后未获得有效的数据库主键"
            );
        }

        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateRequest request) {
        validateUniqueFields(
                request.getUsername(),
                request.getPhone(),
                request.getEmail(),
                id
        );

        User user = new User();
        user.setId(id);
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());

        int affectedRows;

        try {
            affectedRows = userMapper.updateById(user);
        } catch (DuplicateKeyException exception) {
            throw convertDuplicateKeyException(exception);
        }

        if (affectedRows != 1) {
            throw new BusinessException(
                    ResultCode.USER_NOT_FOUND
            );
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        int affectedRows = userMapper.deleteById(id);

        if (affectedRows != 1) {
            throw new BusinessException(
                    ResultCode.USER_NOT_FOUND
            );
        }
    }

    @Override
    public PageResult<UserDetailVO> pageUsers(long page, long size, String username) {
        Page<User> userPage = new Page<>(page, size);

        LambdaQueryWrapper<User> queryWrapper =
                createUserDetailQueryWrapper();
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

    private BusinessException convertDuplicateKeyException(
            DuplicateKeyException exception) {

        if (containsConstraintName(
                exception,
                "uk_sys_user_phone")) {
            return new BusinessException(
                    ResultCode.PHONE_ALREADY_EXISTS
            );
        }

        if (containsConstraintName(
                exception,
                "uk_sys_user_email")) {
            return new BusinessException(
                    ResultCode.EMAIL_ALREADY_EXISTS
            );
        }

        return new BusinessException(
                ResultCode.USERNAME_ALREADY_EXISTS
        );
    }

    private boolean containsConstraintName(
            Throwable throwable,
            String constraintName) {

        Throwable current = throwable;

        while (current != null) {
            String message = current.getMessage();

            if (message != null
                    && message.contains(constraintName)) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private void validateUniqueFields(
            String username,
            String phone,
            String email,
            Long excludedUserId) {

        if (StringUtils.hasText(username)
                && usernameExists(username, excludedUserId)) {
            throw new BusinessException(
                    ResultCode.USERNAME_ALREADY_EXISTS
            );
        }

        if (StringUtils.hasText(phone)
                && phoneExists(phone, excludedUserId)) {
            throw new BusinessException(
                    ResultCode.PHONE_ALREADY_EXISTS
            );
        }

        if (StringUtils.hasText(email)
                && emailExists(email, excludedUserId)) {
            throw new BusinessException(
                    ResultCode.EMAIL_ALREADY_EXISTS
            );
        }
    }

    private boolean usernameExists(
            String username,
            Long excludedUserId) {

        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
                        .ne(
                                excludedUserId != null,
                                User::getId,
                                excludedUserId
                        );

        return userMapper.exists(queryWrapper);
    }

    private boolean phoneExists(
            String phone,
            Long excludedUserId) {

        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, phone)
                        .ne(
                                excludedUserId != null,
                                User::getId,
                                excludedUserId
                        );

        return userMapper.exists(queryWrapper);
    }

    private boolean emailExists(
            String email,
            Long excludedUserId) {
        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<User>()
                        .eq(User::getEmail, email)
                        .ne(
                                excludedUserId != null,
                                User::getId,
                                excludedUserId
                        );

        return userMapper.exists(queryWrapper);
    }

    private LambdaQueryWrapper<User> createUserDetailQueryWrapper() {
        return new LambdaQueryWrapper<User>()
                .select(
                        User::getId,
                        User::getUsername,
                        User::getNickname,
                        User::getPhone,
                        User::getEmail,
                        User::getStatus
                );
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
