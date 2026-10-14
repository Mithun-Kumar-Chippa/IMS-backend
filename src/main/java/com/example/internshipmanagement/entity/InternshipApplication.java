package com.example.internshipmanagement.entity;

import com.example.internshipmanagement.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "internship_applications",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_internship", columnNames = {"internship_id", "student_id"})
    },
    indexes = {
        @Index(name = "idx_app_student", columnList = "student_id"),
        @Index(name = "idx_app_internship", columnList = "internship_id"),
        @Index(name = "idx_app_status", columnList = "status")
    }
)
public class InternshipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id", nullable = false)
    @JsonIgnore
    private Internship internship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private User student;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    @Column(length = 255)
    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    private LocalDateTime reviewedAt;

    @Column(columnDefinition = "TEXT")
    private String reviewerNotes;

    @Column(length = 120)
    private String mentorName;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private InternshipRecord internshipRecord;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
    }

    public InternshipApplication() {}

    public InternshipApplication(Internship internship, User student, String coverLetter, String resumeUrl) {
        this.internship = internship;
        this.student = student;
        this.coverLetter = coverLetter;
        this.resumeUrl = resumeUrl;
        this.status = ApplicationStatus.APPLIED;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Internship getInternship() { return internship; }
    public void setInternship(Internship internship) { this.internship = internship; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getReviewerNotes() { return reviewerNotes; }
    public void setReviewerNotes(String reviewerNotes) { this.reviewerNotes = reviewerNotes; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    public InternshipRecord getInternshipRecord() { return internshipRecord; }
    public void setInternshipRecord(InternshipRecord internshipRecord) { this.internshipRecord = internshipRecord; }
}
