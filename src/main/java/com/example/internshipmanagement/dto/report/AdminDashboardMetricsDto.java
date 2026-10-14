package com.example.internshipmanagement.dto.report;

public class AdminDashboardMetricsDto {
    private long totalStudents;
    private long totalCompanies;
    private long totalInternships;
    private long totalApplications;
    private long pendingCompanyVerifications;
    private long pendingInternshipApprovals;
    private long acceptedApplications;
    private long activeUsers;

    public AdminDashboardMetricsDto() {}

    public AdminDashboardMetricsDto(long totalStudents, long totalCompanies, long totalInternships,
                                     long totalApplications, long pendingCompanyVerifications,
                                     long pendingInternshipApprovals, long acceptedApplications,
                                     long activeUsers) {
        this.totalStudents = totalStudents;
        this.totalCompanies = totalCompanies;
        this.totalInternships = totalInternships;
        this.totalApplications = totalApplications;
        this.pendingCompanyVerifications = pendingCompanyVerifications;
        this.pendingInternshipApprovals = pendingInternshipApprovals;
        this.acceptedApplications = acceptedApplications;
        this.activeUsers = activeUsers;
    }

    // Getters and Setters
    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalCompanies() { return totalCompanies; }
    public void setTotalCompanies(long totalCompanies) { this.totalCompanies = totalCompanies; }

    public long getTotalInternships() { return totalInternships; }
    public void setTotalInternships(long totalInternships) { this.totalInternships = totalInternships; }

    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }

    public long getPendingCompanyVerifications() { return pendingCompanyVerifications; }
    public void setPendingCompanyVerifications(long pendingCompanyVerifications) { this.pendingCompanyVerifications = pendingCompanyVerifications; }

    public long getPendingInternshipApprovals() { return pendingInternshipApprovals; }
    public void setPendingInternshipApprovals(long pendingInternshipApprovals) { this.pendingInternshipApprovals = pendingInternshipApprovals; }

    public long getAcceptedApplications() { return acceptedApplications; }
    public void setAcceptedApplications(long acceptedApplications) { this.acceptedApplications = acceptedApplications; }

    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
}
