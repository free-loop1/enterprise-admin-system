package com.freeloop.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freeloop.admin.common.ResultCode;
import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import com.freeloop.admin.entity.User;
import com.freeloop.admin.exception.BusinessException;
import com.freeloop.admin.mapper.UserMapper;
import com.freeloop.admin.vo.PageResult;
import com.freeloop.admin.vo.UserDetailVO;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @BeforeAll
    static void initializeMybatisPlusTableMetadata() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(
                        new MybatisConfiguration(),
                        ""
                ),
                User.class
        );
    }

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<Wrapper<User>> queryWrapperCaptor;

    @Test
    void shouldReturnUserWhenUserExists() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("alice");

        when(userMapper.selectOne(
                ArgumentMatchers.<Wrapper<User>>any()
        ))
                .thenReturn(expectedUser);

        UserDetailVO actualUser = userService.getById(1L);

        assertEquals(1L, actualUser.getId());
        assertEquals("alice", actualUser.getUsername());

        verify(userMapper).selectOne(queryWrapperCaptor.capture());
        assertPublicUserProjection(queryWrapperCaptor.getValue());
    }

    @Test
    void shouldThrowUserNotFoundWhenUserDoesNotExist() {
        when(userMapper.selectOne(
                ArgumentMatchers.<Wrapper<User>>any()
        ))
                .thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.getById(999L)
        );

        assertEquals(
                ResultCode.USER_NOT_FOUND,
                exception.getResultCode()
        );
        assertEquals(
                "用户不存在",
                exception.getMessage()
        );

        verify(userMapper).selectOne(
                ArgumentMatchers.<Wrapper<User>>any()
        );
    }

    @Test
    void shouldCreateUserWithEncodedPassword() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("bob");
        request.setPassword("Test@123456");
        request.setNickname("Bob");
        request.setPhone("13900139000");
        request.setEmail("bob@example.com");
        request.setStatus(1);

        when(userMapper.exists(any()))
                .thenReturn(false);

        when(passwordEncoder.encode("Test@123456"))
                .thenReturn("encoded-password");

        when(userMapper.insert(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(10L);
                    return 1;
                });

        Long newUserId = userService.createUser(request);

        assertEquals(10L, newUserId);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userMapper).insert(userCaptor.capture());
        verify(passwordEncoder).encode("Test@123456");

        User savedUser = userCaptor.getValue();

        assertEquals("bob", savedUser.getUsername());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals("Bob", savedUser.getNickname());
        assertEquals("13900139000", savedUser.getPhone());
        assertEquals("bob@example.com", savedUser.getEmail());
        assertEquals(1, savedUser.getStatus());
    }

    @Test
    void shouldRejectDuplicateUsernameBeforeEncodingOrInsert() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("alice");
        request.setPassword("Test@123456");

        when(userMapper.exists(any()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.createUser(request)
        );

        assertEquals(
                ResultCode.USERNAME_ALREADY_EXISTS,
                exception.getResultCode()
        );
        assertEquals(
                "用户名已存在",
                exception.getMessage()
        );

        verify(userMapper).exists(any());
        verify(
                passwordEncoder,
                never()
        ).encode(any(CharSequence.class));
        verify(
                userMapper,
                never()
        ).insert(any(User.class));
    }

    @Test
    void shouldConvertDuplicateKeyExceptionWhenInsertConflicts() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("bob");
        request.setPassword("Test@123456");
        request.setNickname("Bob");

        when(userMapper.exists(any()))
                .thenReturn(false);

        when(passwordEncoder.encode("Test@123456"))
                .thenReturn("encoded-password");

        when(userMapper.insert(any(User.class)))
                .thenThrow(
                        new DuplicateKeyException(
                                "Duplicate entry for username"
                        )
                );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.createUser(request)
        );

        assertEquals(
                ResultCode.USERNAME_ALREADY_EXISTS,
                exception.getResultCode()
        );
        assertEquals(
                "用户名已存在",
                exception.getMessage()
        );

        verify(userMapper).exists(any());
        verify(passwordEncoder).encode("Test@123456");
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void shouldFailWhenGeneratedUserIdIsMissing() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("bob");
        request.setPassword("Test@123456");
        request.setNickname("Bob");

        when(userMapper.exists(any()))
                .thenReturn(false);
        when(passwordEncoder.encode("Test@123456"))
                .thenReturn("encoded-password");
        when(userMapper.insert(any(User.class)))
                .thenReturn(1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.createUser(request)
        );

        assertEquals(
                "创建用户后未获得有效的数据库主键",
                exception.getMessage()
        );
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void shouldUpdateProvidedUserFields() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname("Updated Bob");
        request.setPhone("13900139001");
        request.setEmail("updated-bob@example.com");
        request.setStatus(0);

        when(userMapper.updateById(any(User.class)))
                .thenReturn(1);

        userService.updateUser(10L, request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userMapper).updateById(userCaptor.capture());
        verify(userMapper, never()).exists(any());

        User updatedUser = userCaptor.getValue();

        assertEquals(10L, updatedUser.getId());
        assertNull(updatedUser.getUsername());
        assertEquals("Updated Bob", updatedUser.getNickname());
        assertEquals("13900139001", updatedUser.getPhone());
        assertEquals(
                "updated-bob@example.com",
                updatedUser.getEmail()
        );
        assertEquals(0, updatedUser.getStatus());
    }

    @Test
    void shouldThrowUserNotFoundWhenUpdateAffectsNoRows() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname("Nobody");

        when(userMapper.updateById(any(User.class)))
                .thenReturn(0);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.updateUser(999L, request)
        );

        assertEquals(
                ResultCode.USER_NOT_FOUND,
                exception.getResultCode()
        );
        assertEquals(
                "用户不存在",
                exception.getMessage()
        );

        verify(userMapper).updateById(any(User.class));
        verify(userMapper, never()).exists(any());
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        when(userMapper.deleteById(10L))
                .thenReturn(1);

        assertDoesNotThrow(
                () -> userService.deleteUser(10L)
        );

        verify(userMapper).deleteById(10L);
    }

    @Test
    void shouldThrowUserNotFoundWhenDeleteAffectsNoRows() {
        when(userMapper.deleteById(999L))
                .thenReturn(0);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.deleteUser(999L)
        );

        assertEquals(
                ResultCode.USER_NOT_FOUND,
                exception.getResultCode()
        );
        assertEquals(
                "用户不存在",
                exception.getMessage()
        );

        verify(userMapper).deleteById(999L);
    }

    @Test
    void shouldMapUserPageToUserDetailPageResult() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setPassword("encoded-secret");
        user.setNickname("Alice Chen");
        user.setPhone("13800138000");
        user.setEmail("alice@example.com");
        user.setStatus(1);

        Page<User> mapperPage = new Page<>(2, 5, 6);
        mapperPage.setRecords(List.of(user));

        when(userMapper.selectPage(
                ArgumentMatchers.<Page<User>>any(),
                ArgumentMatchers.<Wrapper<User>>any()
        )).thenReturn(mapperPage);

        PageResult<UserDetailVO> result =
                userService.pageUsers(2, 5, "ali");

        assertEquals(2, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(6, result.getTotal());
        assertEquals(2, result.getPages());
        assertEquals(1, result.getRecords().size());

        UserDetailVO userVO = result.getRecords().getFirst();

        assertEquals(1L, userVO.getId());
        assertEquals("alice", userVO.getUsername());
        assertEquals("Alice Chen", userVO.getNickname());
        assertEquals("13800138000", userVO.getPhone());
        assertEquals("alice@example.com", userVO.getEmail());
        assertEquals(1, userVO.getStatus());

        verify(userMapper).selectPage(
                ArgumentMatchers.<Page<User>>any(),
                queryWrapperCaptor.capture()
        );
        assertPublicUserProjection(queryWrapperCaptor.getValue());
    }

    private void assertPublicUserProjection(Wrapper<User> wrapper) {
        LambdaQueryWrapper<?> queryWrapper = assertInstanceOf(
                LambdaQueryWrapper.class,
                wrapper
        );

        String selectedColumns = queryWrapper.getSqlSelect();

        assertNotNull(selectedColumns);
        assertTrue(selectedColumns.contains("username"));
        assertFalse(selectedColumns.contains("password"));
        assertFalse(selectedColumns.contains("deleted"));
    }
}
