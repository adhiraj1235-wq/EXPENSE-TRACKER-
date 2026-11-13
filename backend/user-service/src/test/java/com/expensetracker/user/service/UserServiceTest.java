package com.expensetracker.user.service;

import com.expensetracker.user.dto.AuthResponse;
import com.expensetracker.user.dto.LoginRequest;
import com.expensetracker.user.dto.RegisterRequest;
import com.expensetracker.user.entity.User;
import com.expensetracker.user.exception.BadRequestException;
import com.expensetracker.user.exception.UnauthorizedException;
import com.expensetracker.user.repository.UserRepository;
import com.expensetracker.user.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Rahul Sharma", "rahul@example.com", "encodedPassword123", LocalDateTime.now());
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest("Rahul Sharma", "rahul@example.com", "plainPassword123");

        when(userRepository.existsByEmail("rahul@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtTokenProvider.generateToken(sampleUser)).thenReturn("mock.jwt.token");

        AuthResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("Rahul Sharma", response.getName());
        assertEquals("rahul@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ThrowsBadRequest_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest("Rahul Sharma", "rahul@example.com", "password123");
        when(userRepository.existsByEmail("rahul@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("rahul@example.com", "plainPassword123");

        when(userRepository.findByEmail("rahul@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("plainPassword123", "encodedPassword123")).thenReturn(true);
        when(jwtTokenProvider.generateToken(sampleUser)).thenReturn("mock.jwt.token");

        AuthResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void login_ThrowsUnauthorized_WhenPasswordInvalid() {
        LoginRequest request = new LoginRequest("rahul@example.com", "wrongPassword");

        when(userRepository.findByEmail("rahul@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.login(request));
    }
}

