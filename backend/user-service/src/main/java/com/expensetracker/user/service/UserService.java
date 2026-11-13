package com.expensetracker.user.service;

import com.expensetracker.user.dto.AuthResponse;
import com.expensetracker.user.dto.LoginRequest;
import com.expensetracker.user.dto.RegisterRequest;
import com.expensetracker.user.dto.UserProfileResponse;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserProfileResponse getProfile(Long userId);
}

