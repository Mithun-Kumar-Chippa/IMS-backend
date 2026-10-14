package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.announcement.AnnouncementDto;
import com.example.internshipmanagement.dto.auth.UserSummaryDto;
import com.example.internshipmanagement.dto.common.ApiResponse;
import com.example.internshipmanagement.dto.common.PagedResponse;
import com.example.internshipmanagement.dto.internship.InternshipResponse;
import com.example.internshipmanagement.dto.profile.CompanyProfileDto;
import com.example.internshipmanagement.dto.report.AdminDashboardMetricsDto;
import com.example.internshipmanagement.entity.AuditLog;
import com.example.internshipmanagement.enums.Role;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.AdminService;
import com.example.internshipmanagement.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administrator Operations", description = "Endpoints for platform management, verifications, approvals, and system audit logs")
public class AdminController {

    private final AdminService adminService;
    private final AuditLogService auditLogService;

    public AdminController(AdminService adminService, AuditLogService auditLogService) {
        this.adminService = adminService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get aggregated administrator dashboard metrics")
    public ResponseEntity<ApiResponse<AdminDashboardMetricsDto>> getDashboardMetrics() {
        AdminDashboardMetricsDto metrics = adminService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @GetMapping("/users")
    @Operation(summary = "Get paginated user list with optional role filter")
    public ResponseEntity<ApiResponse<PagedResponse<UserSummaryDto>>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<UserSummaryDto> users = adminService.getAllUsers(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/users/{userId}/active")
    @Operation(summary = "Enable or deactivate a user account")
    public ResponseEntity<ApiResponse<Void>> setUserActiveStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        adminService.setUserActiveStatus(userId, active);
        return ResponseEntity.ok(ApiResponse.success("User account status updated to " + (active ? "ACTIVE" : "INACTIVE"), null));
    }

    @GetMapping("/companies/pending")
    @Operation(summary = "Get companies waiting for administrator verification")
    public ResponseEntity<ApiResponse<PagedResponse<CompanyProfileDto>>> getPendingCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<CompanyProfileDto> pending = adminService.getPendingCompanies(pageable);
        return ResponseEntity.ok(ApiResponse.success(pending));
    }

    @PutMapping("/companies/{id}/verification")
    @Operation(summary = "Verify or reject a company profile")
    public ResponseEntity<ApiResponse<Void>> verifyCompany(
            @PathVariable Long id,
            @RequestParam boolean verify,
            @RequestParam(required = false, defaultValue = "") String notes) {
        adminService.verifyCompany(id, verify, notes);
        return ResponseEntity.ok(ApiResponse.success("Company verification updated to " + verify, null));
    }

    @GetMapping("/internships/pending")
    @Operation(summary = "Get internships waiting for administrator approval")
    public ResponseEntity<ApiResponse<PagedResponse<InternshipResponse>>> getPendingInternships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<InternshipResponse> pending = adminService.getPendingInternships(pageable);
        return ResponseEntity.ok(ApiResponse.success(pending));
    }

    @PutMapping("/internships/{id}/approval")
    @Operation(summary = "Approve or reject an internship posting")
    public ResponseEntity<ApiResponse<Void>> approveInternship(
            @PathVariable Long id,
            @RequestParam boolean approve,
            @RequestParam(required = false, defaultValue = "") String notes) {
        adminService.approveInternship(id, approve, notes);
        return ResponseEntity.ok(ApiResponse.success("Internship approval status updated to " + approve, null));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get system audit logs with pagination")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLog> logs = auditLogService.getAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @PostMapping("/announcements")
    @Operation(summary = "Publish a new system-wide announcement")
    public ResponseEntity<ApiResponse<AnnouncementDto>> createAnnouncement(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AnnouncementDto dto) {
        AnnouncementDto saved = adminService.createAnnouncement(principal, dto);
        return new ResponseEntity<>(ApiResponse.success("Announcement published successfully", saved), HttpStatus.CREATED);
    }

    @GetMapping("/announcements")
    @Operation(summary = "Get all announcements with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<AnnouncementDto>>> getAllAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<AnnouncementDto> announcements = adminService.getAllAnnouncements(pageable);
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }

    @DeleteMapping("/announcements/{id}")
    @Operation(summary = "Delete an announcement")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable Long id) {
        adminService.deleteAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully", null));
    }
}
