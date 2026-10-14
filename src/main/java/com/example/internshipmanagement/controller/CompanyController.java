package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.application.ApplicationResponse;
import com.example.internshipmanagement.dto.common.ApiResponse;
import com.example.internshipmanagement.dto.profile.CompanyProfileDto;
import com.example.internshipmanagement.dto.report.EvaluationDto;
import com.example.internshipmanagement.dto.report.InternshipRecordDto;
import com.example.internshipmanagement.dto.report.ProgressReportDto;
import com.example.internshipmanagement.enums.ApplicationStatus;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Company Operations", description = "Endpoints for company profile, applicants, evaluations, and progress report reviews")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "Get current company profile details")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        CompanyProfileDto profile = companyService.getCompanyProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "Update current company profile details")
    public ResponseEntity<ApiResponse<CompanyProfileDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CompanyProfileDto dto) {
        CompanyProfileDto updated = companyService.updateCompanyProfile(principal.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success("Company profile updated successfully", updated));
    }

    @GetMapping("/applicants")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "View applicants for current company's postings")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getApplicants(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<ApplicationResponse> applicants = companyService.getCompanyApplicants(principal.getId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(applicants));
    }

    @GetMapping("/records")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "View active and completed interns for current company")
    public ResponseEntity<ApiResponse<Page<InternshipRecordDto>>> getCompanyRecords(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InternshipRecordDto> records = companyService.getCompanyInternshipRecords(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @PostMapping("/reports/{reportId}/feedback")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "Review student progress report and provide mentor feedback")
    public ResponseEntity<ApiResponse<ProgressReportDto>> reviewReport(
            @PathVariable Long reportId,
            @RequestParam String feedback,
            @AuthenticationPrincipal UserPrincipal principal) {
        ProgressReportDto updated = companyService.reviewProgressReport(reportId, feedback, principal);
        return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully", updated));
    }

    @PostMapping("/records/{recordId}/evaluation")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "Submit final formal evaluation for completed intern")
    public ResponseEntity<ApiResponse<EvaluationDto>> submitEvaluation(
            @PathVariable Long recordId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EvaluationDto dto) {
        EvaluationDto saved = companyService.submitEvaluation(recordId, dto, principal);
        return ResponseEntity.ok(ApiResponse.success("Evaluation submitted and internship marked completed", saved));
    }

    @GetMapping("/dashboard-stats")
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "Get recruitment overview stats for company dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats(@AuthenticationPrincipal UserPrincipal principal) {
        Map<String, Object> stats = companyService.getCompanyDashboardStats(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
