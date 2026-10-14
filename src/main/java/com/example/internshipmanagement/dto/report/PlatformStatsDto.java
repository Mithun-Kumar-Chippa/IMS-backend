package com.example.internshipmanagement.dto.report;

public class PlatformStatsDto {
    private long totalInternships;
    private long activeCompanies;
    private long registeredStudents;
    private long studentsPlaced;

    public PlatformStatsDto() {}

    public PlatformStatsDto(long totalInternships, long activeCompanies, long registeredStudents, long studentsPlaced) {
        this.totalInternships = totalInternships;
        this.activeCompanies = activeCompanies;
        this.registeredStudents = registeredStudents;
        this.studentsPlaced = studentsPlaced;
    }

    public long getTotalInternships() { return totalInternships; }
    public void setTotalInternships(long totalInternships) { this.totalInternships = totalInternships; }

    public long getActiveCompanies() { return activeCompanies; }
    public void setActiveCompanies(long activeCompanies) { this.activeCompanies = activeCompanies; }

    public long getRegisteredStudents() { return registeredStudents; }
    public void setRegisteredStudents(long registeredStudents) { this.registeredStudents = registeredStudents; }

    public long getStudentsPlaced() { return studentsPlaced; }
    public void setStudentsPlaced(long studentsPlaced) { this.studentsPlaced = studentsPlaced; }
}
