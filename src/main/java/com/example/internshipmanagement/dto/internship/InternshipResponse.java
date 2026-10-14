package com.example.internshipmanagement.dto.internship;

import com.example.internshipmanagement.enums.InternshipStatus;
import com.example.internshipmanagement.enums.WorkMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InternshipResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLocation;
    private String title;
    private String description;
    private String department;
    private String location;
    private WorkMode workMode;
    private Double stipend;
    private Integer durationWeeks;
    private String requirements;
    private String responsibilities;
    private LocalDate deadline;
    private InternshipStatus status;
    private Integer maxPositions;
    private int applicationsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InternshipResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyLocation() { return companyLocation; }
    public void setCompanyLocation(String companyLocation) { this.companyLocation = companyLocation; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public WorkMode getWorkMode() { return workMode; }
    public void setWorkMode(WorkMode workMode) { this.workMode = workMode; }

    public Double getStipend() { return stipend; }
    public void setStipend(Double stipend) { this.stipend = stipend; }

    public Integer getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(Integer durationWeeks) { this.durationWeeks = durationWeeks; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public InternshipStatus getStatus() { return status; }
    public void setStatus(InternshipStatus status) { this.status = status; }

    public Integer getMaxPositions() { return maxPositions; }
    public void setMaxPositions(Integer maxPositions) { this.maxPositions = maxPositions; }

    public int getApplicationsCount() { return applicationsCount; }
    public void setApplicationsCount(int applicationsCount) { this.applicationsCount = applicationsCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
