package com.example.internshipmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_reports", indexes = {
    @Index(name = "idx_report_record", columnList = "record_id")
})
public class ProgressReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    @JsonIgnore
    private InternshipRecord internshipRecord;

    @Column(nullable = false)
    private Integer weekNumber;

    @Column(nullable = false, length = 150)
    private String reportTitle;

    @Column(columnDefinition = "TEXT")
    private String tasksCompleted;

    @Column(columnDefinition = "TEXT")
    private String challengesFaced;

    @Column(columnDefinition = "TEXT")
    private String plansForNextWeek;

    private Integer hoursWorked;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public ProgressReport() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InternshipRecord getInternshipRecord() { return internshipRecord; }
    public void setInternshipRecord(InternshipRecord internshipRecord) { this.internshipRecord = internshipRecord; }

    public Integer getWeekNumber() { return weekNumber; }
    public void setWeekNumber(Integer weekNumber) { this.weekNumber = weekNumber; }

    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }

    public String getTasksCompleted() { return tasksCompleted; }
    public void setTasksCompleted(String tasksCompleted) { this.tasksCompleted = tasksCompleted; }

    public String getChallengesFaced() { return challengesFaced; }
    public void setChallengesFaced(String challengesFaced) { this.challengesFaced = challengesFaced; }

    public String getPlansForNextWeek() { return plansForNextWeek; }
    public void setPlansForNextWeek(String plansForNextWeek) { this.plansForNextWeek = plansForNextWeek; }

    public Integer getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Integer hoursWorked) { this.hoursWorked = hoursWorked; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
