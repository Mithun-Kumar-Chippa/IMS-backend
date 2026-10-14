package com.example.internshipmanagement.dto.auth;

import com.example.internshipmanagement.enums.Role;
import java.time.LocalDateTime;

public class UserSummaryDto {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;

    public UserSummaryDto() {}

    public UserSummaryDto(Long id, String email, String fullName, String phone, Role role, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
