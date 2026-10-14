package com.example.internshipmanagement.dto.application;

import com.example.internshipmanagement.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    private String reviewerNotes;
    private String mentorName;
    private String mentorEmail;

    public StatusUpdateRequest() {}

    public StatusUpdateRequest(ApplicationStatus status, String reviewerNotes) {
        this.status = status;
        this.reviewerNotes = reviewerNotes;
    }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public String getReviewerNotes() { return reviewerNotes; }
    public void setReviewerNotes(String reviewerNotes) { this.reviewerNotes = reviewerNotes; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    public String getMentorEmail() { return mentorEmail; }
    public void setMentorEmail(String mentorEmail) { this.mentorEmail = mentorEmail; }
}
