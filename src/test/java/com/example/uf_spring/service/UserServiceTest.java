package com.example.uf_spring.service;

import com.example.uf_spring.model.User;
import com.example.uf_spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("테스트 사용자");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setAge(25);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        List<User> actualUsers = userService.getAllUsers();

        // Then
        assertThat(actualUsers).hasSize(1);
        assertThat(actualUsers.get(0).getName()).isEqualTo("테스트 사용자");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("테스트 사용자");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User createdUser = userService.createUser(testUser);

        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("테스트 사용자");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void getUsersByAgeGreaterThan_ShouldReturnFilteredUsers() {
        // Given
        User olderUser = new User();
        olderUser.setAge(30);
        List<User> allUsers = Arrays.asList(testUser, olderUser);
        when(userRepository.findByAgeGreaterThanEqual(25)).thenReturn(allUsers);

        // When
        List<User> result = userService.getUsersByAgeGreaterThan(25);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(user -> user.getAge() >= 25);
        verify(userRepository, times(1)).findByAgeGreaterThanEqual(25);
    }
} 