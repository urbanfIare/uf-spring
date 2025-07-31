package com.example.uf_spring.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtConfigTest {

    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        ReflectionTestUtils.setField(jwtConfig, "secret", "your-super-secret-jwt-key-here-make-it-long-enough-for-hs256-algorithm");
        ReflectionTestUtils.setField(jwtConfig, "expiration", 86400000L);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Given
        String email = "test@example.com";
        String role = "USER";

        // When
        String token = jwtConfig.generateToken(email, role);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {
        // Given
        String email = "test@example.com";
        String role = "USER";
        String token = jwtConfig.generateToken(email, role);

        // When
        String extractedEmail = jwtConfig.extractEmail(token);

        // Then
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void extractRole_ShouldReturnCorrectRole() {
        // Given
        String email = "test@example.com";
        String role = "ADMIN";
        String token = jwtConfig.generateToken(email, role);

        // When
        String extractedRole = jwtConfig.extractRole(token);

        // Then
        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String email = "test@example.com";
        String role = "USER";
        String token = jwtConfig.generateToken(email, role);

        // When
        boolean isValid = jwtConfig.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtConfig.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        // Given
        String email = "test@example.com";

        // When
        String token = jwtConfig.generateRefreshToken(email);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(jwtConfig.isRefreshToken(token)).isTrue();
    }

    @Test
    void isRefreshToken_WithAccessToken_ShouldReturnFalse() {
        // Given
        String email = "test@example.com";
        String role = "USER";
        String token = jwtConfig.generateToken(email, role);

        // When
        boolean isRefresh = jwtConfig.isRefreshToken(token);

        // Then
        assertThat(isRefresh).isFalse();
    }
} 