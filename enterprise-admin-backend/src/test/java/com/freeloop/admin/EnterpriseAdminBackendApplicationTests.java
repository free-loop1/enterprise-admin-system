package com.freeloop.admin;

import com.freeloop.admin.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class EnterpriseAdminBackendApplicationTests {
    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldBindJwtProperties() {
        assertThat(jwtProperties.getSecret())
                .isNotBlank();

        assertThat(jwtProperties.getAccessTokenExpirationMinutes())
                .isEqualTo(30);
    }

}
