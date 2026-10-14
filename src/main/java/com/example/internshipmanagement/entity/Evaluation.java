package com.example.internshipmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    @JsonIgnore
    private InternshipRecord internshipRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    @JsonIgnore
    private User evaluatedBy;

    private Integer technicalSkillRating; // 1 - 5
    private Integer softSkillRating;      // 1 - 5
    private Integer punctualityRating;    // 1 - 5
    private Integer overallRating;        // 1 - 5

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(length = 100)
    private String recommendation; // e.g. "HIRED_FULL_TIME", "EXTEND_INTERNSHIP", "SUCCESSFULLY_COMPLETED"

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Evaluation() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InternshipRecord getInternshipRecord() { return internshipRecord; }
    public void setInternshipRecord(InternshipRecord internshipRecord) { this.internshipRecord = internshipRecord; }

    public User getEvaluatedBy() { return evaluatedBy; }
    public void setEvaluatedBy(User evaluatedBy) { this.evaluatedBy = evaluatedBy; }

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
