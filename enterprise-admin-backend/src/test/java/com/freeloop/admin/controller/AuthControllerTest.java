package com.freeloop.admin.controller;

import com.freeloop.admin.common.ResultCode;
import com.freeloop.admin.dto.RegisterRequest;
import com.freeloop.admin.exception.BusinessException;
import com.freeloop.admin.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldRegisterUserAndReturnLocation() throws Exception {
        String requestBody = """
                {
                  "username": "alice",
                  "password": "Test@123456",
                  "nickname": "Alice",
                  "phone": "13800138000",
                  "email": "alice@example.com"
                }
                """;

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(10L);

        mockMvc.perform(
                        post("/api/auth/register")
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

        ArgumentCaptor<RegisterRequest> requestCaptor =
                ArgumentCaptor.forClass(RegisterRequest.class);

        verify(authService).register(requestCaptor.capture());

        RegisterRequest capturedRequest =
                requestCaptor.getValue();

        assertEquals("alice", capturedRequest.getUsername());
        assertEquals("Test@123456", capturedRequest.getPassword());
        assertEquals("Alice", capturedRequest.getNickname());
        assertEquals("13800138000", capturedRequest.getPhone());
        assertEquals(
                "alice@example.com",
                capturedRequest.getEmail()
        );
    }

    @Test
    void shouldRejectInvalidRegisterRequest() throws Exception {
        String requestBody = """
                {
                  "username": "",
                  "password": "123",
                  "nickname": "",
                  "phone": "123",
                  "email": "invalid"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldReturnConflictWhenUsernameAlreadyExists() throws Exception {
        String requestBody = """
                {
                  "username": "alice",
                  "password": "Test@123456",
                  "nickname": "Alice",
                  "phone": "13800138000",
                  "email": "alice@example.com"
                }
                """;

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException(
                        ResultCode.USERNAME_ALREADY_EXISTS
                ));

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40901))
                .andExpect(jsonPath("$.message").value("用户名已存在"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void shouldReturnConflictWhenPhoneAlreadyExists() throws Exception {
        String requestBody = """
                {
                  "username": "alice",
                  "password": "Test@123456",
                  "nickname": "Alice",
                  "phone": "13800138000",
                  "email": "alice@example.com"
                }
                """;

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException(
                        ResultCode.PHONE_ALREADY_EXISTS
                ));

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40902))
                .andExpect(jsonPath("$.message").value("手机号已存在"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        String requestBody = """
                {
                  "username": "alice",
                  "password": "Test@123456",
                  "nickname": "Alice",
                  "phone": "13800138000",
                  "email": "alice@example.com"
                }
                """;

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException(
                        ResultCode.EMAIL_ALREADY_EXISTS
                ));

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40903))
                .andExpect(jsonPath("$.message").value("邮箱已存在"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
