package com.example.internshipmanagement.dto.auth;

import com.example.internshipmanagement.enums.Role;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private boolean active;
    private boolean verified; // For company

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String email, String fullName, Role role, boolean active) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
        this.verified = true;
    }

    public AuthResponse(String token, Long id, String email, String fullName, Role role, boolean active, boolean verified) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
        this.verified = verified;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
