package com.example.internshipmanagement.dto.application;

import com.example.internshipmanagement.enums.ApplicationStatus;
import java.time.LocalDateTime;

public class ApplicationResponse {
    private Long id;
    private Long internshipId;
    private String internshipTitle;
    private Long companyId;
    private String companyName;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentPhone;
    private String studentCollege;
    private String studentSkills;
    private String coverLetter;
    private String resumeUrl;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private String reviewerNotes;
    private String mentorName;
    private Long recordId;

    public ApplicationResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInternshipId() { return internshipId; }
    public void setInternshipId(Long internshipId) { this.internshipId = internshipId; }

    public String getInternshipTitle() { return internshipTitle; }
    public void setInternshipTitle(String internshipTitle) { this.internshipTitle = internshipTitle; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getStudentPhone() { return studentPhone; }
    public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }

    public String getStudentCollege() { return studentCollege; }
    public void setStudentCollege(String studentCollege) { this.studentCollege = studentCollege; }

    public String getStudentSkills() { return studentSkills; }
    public void setStudentSkills(String studentSkills) { this.studentSkills = studentSkills; }

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

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
}
