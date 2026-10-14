package com.example.internshipmanagement.dto.report;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ProgressReportDto {
    private Long id;
    private Long recordId;

    @NotNull(message = "Week number is required")
    @Min(value = 1, message = "Week number must be at least 1")
    private Integer weekNumber;

    @NotBlank(message = "Report title is required")
    private String reportTitle;

    @NotBlank(message = "Tasks completed is required")
    private String tasksCompleted;

    private String challengesFaced;
    private String plansForNextWeek;
    private Integer hoursWorked;
    private LocalDateTime submittedAt;
    private String feedback;
    private LocalDateTime reviewedAt;

    public ProgressReportDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

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
