package com.example.internshipmanagement.dto.report;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class EvaluationDto {
    private Long id;
    private Long recordId;
    private Long evaluatorId;
    private String evaluatorName;

    @NotNull(message = "Technical skill rating is required")
    @Min(1) @Max(5)
    private Integer technicalSkillRating;

    @NotNull(message = "Soft skill rating is required")
    @Min(1) @Max(5)
    private Integer softSkillRating;

    @NotNull(message = "Punctuality rating is required")
    @Min(1) @Max(5)
    private Integer punctualityRating;

    @NotNull(message = "Overall rating is required")
    @Min(1) @Max(5)
    private Integer overallRating;

    private String comments;
    private String recommendation;
    private LocalDateTime createdAt;

    public EvaluationDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public Long getEvaluatorId() { return evaluatorId; }
    public void setEvaluatorId(Long evaluatorId) { this.evaluatorId = evaluatorId; }

    public String getEvaluatorName() { return evaluatorName; }
    public void setEvaluatorName(String evaluatorName) { this.evaluatorName = evaluatorName; }

    public Integer getTechnicalSkillRating() { return technicalSkillRating; }
    public void setTechnicalSkillRating(Integer technicalSkillRating) { this.technicalSkillRating = technicalSkillRating; }

    public Integer getSoftSkillRating() { return softSkillRating; }
    public void setSoftSkillRating(Integer softSkillRating) { this.softSkillRating = softSkillRating; }

    public Integer getPunctualityRating() { return punctualityRating; }
    public void setPunctualityRating(Integer punctualityRating) { this.punctualityRating = punctualityRating; }

    public Integer getOverallRating() { return overallRating; }
    public void setOverallRating(Integer overallRating) { this.overallRating = overallRating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
