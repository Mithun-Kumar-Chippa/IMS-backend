package com.example.internshipmanagement.dto.internship;

import com.example.internshipmanagement.enums.WorkMode;

public class InternshipFilterDto {
    private String keyword;
    private String location;
    private WorkMode workMode;
    private Double minStipend;
    private boolean activeOnly = true;

    public InternshipFilterDto() {}

    public InternshipFilterDto(String keyword, String location, WorkMode workMode, Double minStipend, boolean activeOnly) {
        this.keyword = keyword;
        this.location = location;
        this.workMode = workMode;
        this.minStipend = minStipend;
        this.activeOnly = activeOnly;
    }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public WorkMode getWorkMode() { return workMode; }
    public void setWorkMode(WorkMode workMode) { this.workMode = workMode; }

    public Double getMinStipend() { return minStipend; }
    public void setMinStipend(Double minStipend) { this.minStipend = minStipend; }

    public boolean isActiveOnly() { return activeOnly; }
    public void setActiveOnly(boolean activeOnly) { this.activeOnly = activeOnly; }
}
