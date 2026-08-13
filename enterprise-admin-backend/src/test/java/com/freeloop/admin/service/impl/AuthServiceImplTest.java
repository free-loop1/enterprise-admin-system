package com.freeloop.admin.service.impl;

import com.freeloop.admin.dto.RegisterRequest;
import com.freeloop.admin.dto.UserCreateRequest;
import com.freeloop.admin.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<UserCreateRequest> requestCaptor;

    @Test
    void shouldRegisterEnabledUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setPassword("Test@123456");
        request.setNickname("Alice");
        request.setPhone("13800138000");
        request.setEmail("alice@example.com");

        when(userService.createUser(any(UserCreateRequest.class)))
                .thenReturn(10L);

        Long userId = authService.register(request);

        assertEquals(10L, userId);

        verify(userService).createUser(requestCaptor.capture());

        UserCreateRequest createRequest = requestCaptor.getValue();

        assertEquals("alice", createRequest.getUsername());
        assertEquals("Test@123456", createRequest.getPassword());
        assertEquals("Alice", createRequest.getNickname());
        assertEquals("13800138000", createRequest.getPhone());
        assertEquals("alice@example.com", createRequest.getEmail());
        assertEquals(1, createRequest.getStatus());
    }
}


