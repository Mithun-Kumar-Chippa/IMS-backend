package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.auth.*;
import com.example.internshipmanagement.dto.common.ApiResponse;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and session management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/student")
    @Operation(summary = "Register a new Student")
    public ResponseEntity<ApiResponse<AuthResponse>> registerStudent(@Valid @RequestBody RegisterStudentRequest request) {
        AuthResponse response = authService.registerStudent(request);
        return new ResponseEntity<>(ApiResponse.success("Student registered successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/register/company")
    @Operation(summary = "Register a new Company")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {
        AuthResponse response = authService.registerCompany(request);
        return new ResponseEntity<>(ApiResponse.success("Company registered successfully. Pending administrator verification.", response), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and receive JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile summary")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserSummaryDto userSummary = authService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success(userSummary));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change current user's password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
