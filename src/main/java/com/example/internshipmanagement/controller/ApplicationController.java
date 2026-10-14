package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.application.ApplicationRequest;
import com.example.internshipmanagement.dto.application.ApplicationResponse;
import com.example.internshipmanagement.dto.application.StatusUpdateRequest;
import com.example.internshipmanagement.dto.common.ApiResponse;
import com.example.internshipmanagement.dto.common.PagedResponse;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
@Tag(name = "Applications", description = "Endpoints for submitting and managing internship applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit a new internship application (Student only)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyForInternship(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.applyForInternship(principal, request);
        return new ResponseEntity<>(ApiResponse.success("Application submitted successfully!", response), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student's submitted applications")
    public ResponseEntity<ApiResponse<PagedResponse<ApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        PagedResponse<ApplicationResponse> response = applicationService.getStudentApplications(principal, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific application details (Authorized student, company, or admin)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        ApplicationResponse response = applicationService.getApplicationById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "Update application status (Company or Admin only)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StatusUpdateRequest request) {
        ApplicationResponse response = applicationService.updateApplicationStatus(id, principal, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated to " + request.getStatus(), response));
    }

    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Withdraw an active application (Student only)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> withdrawApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        ApplicationResponse response = applicationService.withdrawApplication(id, principal);
        return ResponseEntity.ok(ApiResponse.success("Application successfully withdrawn", response));
    }
}
