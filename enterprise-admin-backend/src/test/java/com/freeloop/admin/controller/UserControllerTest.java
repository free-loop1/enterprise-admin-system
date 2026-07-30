package com.freeloop.admin.controller;

import com.freeloop.admin.common.ResultCode;
import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.dto.UserUpdateRequest;
import com.freeloop.admin.exception.BusinessException;
import com.freeloop.admin.service.UserService;
import com.freeloop.admin.vo.PageResult;
import com.freeloop.admin.vo.UserDetailVO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldReturnUnifiedUserDetailResponse() throws Exception {
        UserDetailVO user = new UserDetailVO();
        user.setId(1L);
        user.setUsername("alice");
        user.setNickname("Alice Chen");
        user.setPhone("13800138000");
        user.setEmail("alice@example.com");
        user.setStatus(1);

        when(userService.getById(1L))
                .thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.nickname").value("Alice Chen"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"))
                .andExpect(jsonPath("$.data.email").value("alice@example.com"))
                .andExpect(jsonPath("$.data.status").value(1))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.deleted").doesNotExist());

        verify(userService).getById(1L);
    }

    @Test
    void shouldReturnUnifiedNotFoundResponse() throws Exception {
        when(userService.getById(999L))
                .thenThrow(
                        new BusinessException(
                                ResultCode.USER_NOT_FOUND
                        )
                );

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(40401))
                .andExpect(jsonPath("$.message").value("用户不存在"))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(userService).getById(999L);
    }

    @Test
    void shouldReturnValidationErrorForNonPositiveUserId() throws Exception {

        mockMvc.perform(get("/api/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(40001))
                .andExpect(
                        jsonPath("$.message")
                                .value("用户 ID 必须大于 0")
                )
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(
                userService,
                never()
        ).getById(anyLong());
    }

    @Test
    void shouldReturnValidationErrorForInvalidCreateRequest()
            throws Exception {

        String requestBody = """
                {
                  "username": "testuser",
                  "password": "Test@123456",
                  "nickname": "Test User",
                  "status": 2
                }
                """;

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(40001))
                .andExpect(
                        jsonPath("$.message")
                                .value("状态只能是 0 或 1")
                )
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(
                userService,
                never()
        ).createUser(any(UserCreateRequest.class));
    }

    @Test
    void shouldCreateUserAndReturnLocation() throws Exception {
        String requestBody = """
                {
                  "username": "bob",
                  "password": "Test@123456",
                  "nickname": "Bob",
                  "phone": "13900139000",
                  "email": "bob@example.com",
                  "status": 1
                }
                """;

        when(userService.createUser(any(UserCreateRequest.class)))
                .thenReturn(10L);

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/users/10"
                ))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value(10));

        ArgumentCaptor<UserCreateRequest> requestCaptor =
                ArgumentCaptor.forClass(UserCreateRequest.class);

        verify(userService).createUser(requestCaptor.capture());

        UserCreateRequest capturedRequest =
                requestCaptor.getValue();

        assertEquals("bob", capturedRequest.getUsername());
        assertEquals("Test@123456", capturedRequest.getPassword());
        assertEquals("Bob", capturedRequest.getNickname());
        assertEquals("13900139000", capturedRequest.getPhone());
        assertEquals(
                "bob@example.com",
                capturedRequest.getEmail()
        );
        assertEquals(1, capturedRequest.getStatus());
    }

    @Test
    void shouldReturnUnifiedPagedUserResponse() throws Exception {
        UserDetailVO userVO = new UserDetailVO();
        userVO.setId(1L);
        userVO.setUsername("alice");
        userVO.setNickname("Alice Chen");
        userVO.setPhone("13800138000");
        userVO.setEmail("alice@example.com");
        userVO.setStatus(1);

        PageResult<UserDetailVO> pageResult =
                new PageResult<>();
        pageResult.setRecords(List.of(userVO));
        pageResult.setTotal(6);
        pageResult.setPage(2);
        pageResult.setSize(5);
        pageResult.setPages(2);

        when(userService.pageUsers(2, 5, "ali"))
                .thenReturn(pageResult);

        mockMvc.perform(
                        get("/api/users")
                                .param("page", "2")
                                .param("size", "5")
                                .param("username", "ali")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.page").value(2))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.total").value(6))
                .andExpect(jsonPath("$.data.pages").value(2))
                .andExpect(jsonPath("$.data.records.length()").value(1))
                .andExpect(
                        jsonPath("$.data.records[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.data.records[0].username")
                                .value("alice")
                )
                .andExpect(
                        jsonPath("$.data.records[0].password")
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath("$.data.records[0].deleted")
                                .doesNotExist()
                );

        verify(userService).pageUsers(2, 5, "ali");
    }

    @Test
    void shouldUpdateUserAndReturnUnifiedSuccessResponse()
            throws Exception {

        String requestBody = """
                {
                  "nickname": "Updated Bob",
                  "status": 0
                }
                """;

        mockMvc.perform(
                        put("/api/users/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value(nullValue()));

        ArgumentCaptor<UserUpdateRequest> requestCaptor =
                ArgumentCaptor.forClass(UserUpdateRequest.class);

        verify(userService).updateUser(
                eq(10L),
                requestCaptor.capture()
        );

        UserUpdateRequest capturedRequest =
                requestCaptor.getValue();

        assertEquals(
                "Updated Bob",
                capturedRequest.getNickname()
        );
        assertEquals(0, capturedRequest.getStatus());
    }

    @Test
    void shouldDeleteUserAndReturnUnifiedSuccessResponse()
            throws Exception {

        mockMvc.perform(delete("/api/users/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(userService).deleteUser(10L);
    }
}
