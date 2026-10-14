package com.example.internshipmanagement.dto.report;

import com.example.internshipmanagement.enums.RecordStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InternshipRecordDto {
    private Long id;
    private Long applicationId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long companyId;
    private String companyName;
    private Long internshipId;
    private String internshipTitle;
    private String mentorName;
    private String mentorEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private RecordStatus status;
    private String finalRemarks;
    private int progressReportsCount;
    private boolean evaluated;
    private LocalDateTime createdAt;

    public InternshipRecordDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Long getInternshipId() { return internshipId; }
    public void setInternshipId(Long internshipId) { this.internshipId = internshipId; }

    public String getInternshipTitle() { return internshipTitle; }
    public void setInternshipTitle(String internshipTitle) { this.internshipTitle = internshipTitle; }

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

    public int getProgressReportsCount() { return progressReportsCount; }
    public void setProgressReportsCount(int progressReportsCount) { this.progressReportsCount = progressReportsCount; }

    public boolean isEvaluated() { return evaluated; }
    public void setEvaluated(boolean evaluated) { this.evaluated = evaluated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
