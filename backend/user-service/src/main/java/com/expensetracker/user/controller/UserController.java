package com.expensetracker.user.controller;

import com.expensetracker.user.dto.UserProfileResponse;
import com.expensetracker.user.exception.UnauthorizedException;
import com.expensetracker.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "Endpoints for user profile retrieval and management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile by X-User-Id header")
    public ResponseEntity<UserProfileResponse> getProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new UnauthorizedException("User ID missing from request header");
        }
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}

