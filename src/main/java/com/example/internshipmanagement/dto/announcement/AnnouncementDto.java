package com.example.internshipmanagement.dto.announcement;

import com.example.internshipmanagement.enums.Role;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class AnnouncementDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private Role targetRole;
    private String authorName;
    private boolean active = true;
    private LocalDateTime createdAt;

    public AnnouncementDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Role getTargetRole() { return targetRole; }
    public void setTargetRole(Role targetRole) { this.targetRole = targetRole; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
