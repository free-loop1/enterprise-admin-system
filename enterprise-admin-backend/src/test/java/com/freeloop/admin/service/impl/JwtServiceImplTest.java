package com.freeloop.admin.service.impl;

import com.freeloop.admin.config.JwtProperties;
import com.freeloop.admin.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceImplTest {

    private static final String TEST_SECRET =
            "ZW50ZXJwcmlzZS1hZG1pbi1kZXYtc2VjcmV0LWtleS0yMDI2IQ==";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(TEST_SECRET);
        properties.setAccessTokenExpirationMinutes(30);

        jwtService = new JwtServiceImpl(properties);
    }

    @Test
    void shouldGenerateThreePartAccessToken() {
        String token = jwtService.generateAccessToken(
                5L,
                "alice",
                0
        );

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldParseAccessTokenClaims() {
        String token = jwtService.generateAccessToken(
                5L,
                "alice",
                0
        );

        Claims claims = jwtService.parseAccessToken(token);

        assertThat(claims.getSubject())
                .isEqualTo("5");

        assertThat(claims.get("username", String.class))
                .isEqualTo("alice");

        assertThat(claims.get("tokenVersion", Integer.class))
                .isEqualTo(0);

        assertThat(claims.getIssuedAt())
                .isNotNull();

        assertThat(claims.getExpiration())
                .isAfter(claims.getIssuedAt());

        Duration validity = Duration.between(
                claims.getIssuedAt().toInstant(),
                claims.getExpiration().toInstant()
        );

        assertThat(validity.toMinutes())
                .isEqualTo(30);
    }

    @Test
    void shouldRejectTokenSignedWithDifferentKey() {
        JwtProperties otherProperties = new JwtProperties();
        otherProperties.setSecret(
                "YW5vdGhlci1lbnRlcnByaXNlLWFkbWluLXNlY3JldC1rZXktMjAyNiE="
        );
        otherProperties.setAccessTokenExpirationMinutes(30);

        JwtService otherJwtService =
                new JwtServiceImpl(otherProperties);

        String token = otherJwtService.generateAccessToken(
                5L,
                "alice",
                0
        );

        assertThatThrownBy(
                () -> jwtService.parseAccessToken(token)
        ).isInstanceOf(SignatureException.class);
    }

    @Test
    void shouldRejectExpiredAccessToken() {
        SecretKey key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(TEST_SECRET)
        );

        Instant now = Instant.now();

        String expiredToken = Jwts.builder()
                .subject("5")
                .issuedAt(
                        Date.from(
                                now.minus(2, ChronoUnit.MINUTES)
                        )
                )
                .expiration(
                        Date.from(
                                now.minus(1, ChronoUnit.MINUTES)
                        )
                )
                .signWith(key)
                .compact();

        assertThatThrownBy(
                () -> jwtService.parseAccessToken(expiredToken)
        ).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void shouldRejectMalformedAccessToken() {
        String malformedToken = "not-a-valid-jwt";

        assertThatThrownBy(
                () -> jwtService.parseAccessToken(malformedToken)
        ).isInstanceOf(MalformedJwtException.class);
    }

    @Test
    void shouldRejectEmptyAccessToken() {
        assertThatThrownBy(
                () -> jwtService.parseAccessToken("")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}