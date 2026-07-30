package com.freeloop.admin;

import com.freeloop.admin.service.UserService;
import com.freeloop.admin.vo.UserDetailVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("dev")
class DatabaseConnectionIT {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserService userService;

    @Test
    void shouldConnectToEnterpriseAdminDatabase() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(2));
            assertEquals("enterprise_admin", connection.getCatalog());
        }
    }

    @Test
    void shouldGetUserByIdThroughService() {
        UserDetailVO user = userService.getById(1L);

        assertNotNull(user);
        assertEquals("alice", user.getUsername());
    }
}
