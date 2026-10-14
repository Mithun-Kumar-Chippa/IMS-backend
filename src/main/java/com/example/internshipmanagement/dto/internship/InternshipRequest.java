package com.example.internshipmanagement.dto.internship;

import com.example.internshipmanagement.enums.WorkMode;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class InternshipRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String department;
    private String location;

    @NotNull(message = "Work mode is required")
    private WorkMode workMode;

    @Min(value = 0, message = "Stipend cannot be negative")
    private Double stipend;

    @Min(value = 1, message = "Duration must be at least 1 week")
    private Integer durationWeeks;

    private String requirements;
    private String responsibilities;

    @FutureOrPresent(message = "Deadline must be today or in the future")
    private LocalDate deadline;

    @Min(value = 1, message = "Positions must be at least 1")
    private Integer maxPositions = 1;

    public InternshipRequest() {}

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public WorkMode getWorkMode() { return workMode; }
    public void setWorkMode(WorkMode workMode) { this.workMode = workMode; }

    public Double getStipend() { return stipend; }
    public void setStipend(Double stipend) { this.stipend = stipend; }

    public Integer getDurationWeeks() { return durationWeeks; }
    public void setDurationWeeks(Integer durationWeeks) { this.durationWeeks = durationWeeks; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public Integer getMaxPositions() { return maxPositions; }
    public void setMaxPositions(Integer maxPositions) { this.maxPositions = maxPositions; }
}
