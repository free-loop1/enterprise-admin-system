package com.freeloop.admin.service;

import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import com.freeloop.admin.entity.User;
import com.freeloop.admin.vo.PageResult;
import com.freeloop.admin.vo.UserDetailVO;

public interface UserService {

    User getById(Long id);

    Long createUser(UserCreateRequest request);

    boolean updateUser(Long id, UserUpdateRequest request);

    boolean deleteUser(Long id);

    PageResult<UserDetailVO> pageUsers(long page, long size, String username);
}
