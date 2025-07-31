package com.example.uf_spring.service;

import com.example.uf_spring.dto.RegisterRequest;
import com.example.uf_spring.model.Role;
import com.example.uf_spring.model.User;
import com.example.uf_spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("테스트 사용자");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setAge(25);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("테스트 사용자");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setAge(25);
        mockUser.setRole(Role.USER);
    }

    @Test
    void register_ShouldCreateNewUser() {
        // given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // when
        User result = authService.register(registerRequest);

        // then
        assertNotNull(result);
        assertEquals(registerRequest.getName(), result.getName());
        assertEquals(registerRequest.getEmail(), result.getEmail());
        assertEquals(Role.USER, result.getRole());
        
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_ShouldReturnUser_WhenValidCredentials() {
        // given
        String email = "test@example.com";
        String password = "password123";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);

        // when
        User result = authService.authenticate(email, password);

        // then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, mockUser.getPassword());
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // given
        String email = "nonexistent@example.com";
        String password = "password123";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(email, password);
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void authenticate_ShouldThrowException_WhenInvalidPassword() {
        // given
        String email = "test@example.com";
        String password = "wrongpassword";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(false);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(email, password);
        });

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, mockUser.getPassword());
    }

    @Test
    void verifyPassword_ShouldReturnTrue_WhenPasswordMatches() {
        // given
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // when
        boolean result = authService.verifyPassword(rawPassword, encodedPassword);

        // then
        assertTrue(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void verifyPassword_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        // given
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // when
        boolean result = authService.verifyPassword(rawPassword, encodedPassword);

        // then
        assertFalse(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }
} 