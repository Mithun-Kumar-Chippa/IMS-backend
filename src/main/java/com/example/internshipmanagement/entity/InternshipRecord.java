package com.example.internshipmanagement.entity;

import com.example.internshipmanagement.enums.RecordStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internship_records", indexes = {
    @Index(name = "idx_record_student", columnList = "student_id"),
    @Index(name = "idx_record_company", columnList = "company_id"),
    @Index(name = "idx_record_status", columnList = "status")
})
public class InternshipRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    @JsonIgnore
    private InternshipApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnore
    private User company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id", nullable = false)
    @JsonIgnore
    private Internship internship;

    @Column(length = 120)
    private String mentorName;

    @Column(length = 120)
    private String mentorEmail;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private RecordStatus status = RecordStatus.IN_PROGRESS;

    @Column(columnDefinition = "TEXT")
    private String finalRemarks;

    @OneToMany(mappedBy = "internshipRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ProgressReport> progressReports = new ArrayList<>();

    @OneToOne(mappedBy = "internshipRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Evaluation evaluation;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public InternshipRecord() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InternshipApplication getApplication() { return application; }
    public void setApplication(InternshipApplication application) { this.application = application; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public User getCompany() { return company; }
    public void setCompany(User company) { this.company = company; }

    public Internship getInternship() { return internship; }
    public void setInternship(Internship internship) { this.internship = internship; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    public String getMentorEmail() { return mentorEmail; }
    public void setMentorEmail(String mentorEmail) { this.mentorEmail = mentorEmail; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }

    public String getFinalRemarks() { return finalRemarks; }
    public void setFinalRemarks(String finalRemarks) { this.finalRemarks = finalRemarks; }

    public List<ProgressReport> getProgressReports() { return progressReports; }
    public void setProgressReports(List<ProgressReport> progressReports) { this.progressReports = progressReports; }

    public Evaluation getEvaluation() { return evaluation; }
    public void setEvaluation(Evaluation evaluation) { this.evaluation = evaluation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
