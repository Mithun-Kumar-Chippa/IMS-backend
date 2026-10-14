package com.example.internshipmanagement.dto.application;

import jakarta.validation.constraints.NotNull;

public class ApplicationRequest {

    @NotNull(message = "Internship ID is required")
    private Long internshipId;

    private String coverLetter;
    private String resumeUrl;

    public ApplicationRequest() {}

    public ApplicationRequest(Long internshipId, String coverLetter, String resumeUrl) {
        this.internshipId = internshipId;
        this.coverLetter = coverLetter;
        this.resumeUrl = resumeUrl;
    }

    public Long getInternshipId() { return internshipId; }
    public void setInternshipId(Long internshipId) { this.internshipId = internshipId; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
}
