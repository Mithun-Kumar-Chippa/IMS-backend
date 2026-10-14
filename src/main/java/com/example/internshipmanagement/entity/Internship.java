package com.example.internshipmanagement.entity;

import com.example.internshipmanagement.enums.InternshipStatus;
import com.example.internshipmanagement.enums.WorkMode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internships", indexes = {
    @Index(name = "idx_internship_status", columnList = "status"),
    @Index(name = "idx_internship_company", columnList = "company_id"),
    @Index(name = "idx_internship_deadline", columnList = "deadline")
})
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnore
    private User company;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 100)
    private String department;

    @Column(length = 150)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkMode workMode;

    private Double stipend;

    private Integer durationWeeks;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private InternshipStatus status = InternshipStatus.PENDING_APPROVAL;

    private Integer maxPositions = 1;

    @OneToMany(mappedBy = "internship", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<InternshipApplication> applications = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Internship() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getCompany() { return company; }
    public void setCompany(User company) { this.company = company; }

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

    public List<InternshipApplication> getApplications() { return applications; }
    public void setApplications(List<InternshipApplication> applications) { this.applications = applications; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
