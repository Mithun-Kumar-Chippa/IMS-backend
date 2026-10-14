package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.common.ApiResponse;
import com.example.internshipmanagement.dto.common.PagedResponse;
import com.example.internshipmanagement.dto.internship.InternshipFilterDto;
import com.example.internshipmanagement.dto.internship.InternshipRequest;
import com.example.internshipmanagement.dto.internship.InternshipResponse;
import com.example.internshipmanagement.dto.report.PlatformStatsDto;
import com.example.internshipmanagement.enums.WorkMode;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.InternshipService;
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
@RequestMapping("/api/v1/internships")
@Tag(name = "Internships", description = "Endpoints for discovering, creating, updating, and managing internship opportunities")
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @GetMapping
    @Operation(summary = "Search and filter approved internships with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<InternshipResponse>>> searchInternships(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) WorkMode workMode,
            @RequestParam(required = false) Double minStipend,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        InternshipFilterDto filter = new InternshipFilterDto();
        filter.setKeyword(keyword);
        filter.setLocation(location);
        filter.setWorkMode(workMode);
        filter.setMinStipend(minStipend);
        filter.setActiveOnly(activeOnly);

        PagedResponse<InternshipResponse> response = internshipService.searchInternships(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed information for a specific internship")
    public ResponseEntity<ApiResponse<InternshipResponse>> getInternshipById(@PathVariable Long id) {
        InternshipResponse response = internshipService.getInternshipById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get aggregated platform statistics for landing page")
    public ResponseEntity<ApiResponse<PlatformStatsDto>> getPlatformStats() {
        PlatformStatsDto stats = internshipService.getPlatformStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @PostMapping
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "Post a new internship opportunity (Company/Admin only)")
    public ResponseEntity<ApiResponse<InternshipResponse>> createInternship(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody InternshipRequest request) {
        InternshipResponse response = internshipService.createInternship(principal, request);
        return new ResponseEntity<>(ApiResponse.success("Internship opportunity posted successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "Update an existing internship opportunity")
    public ResponseEntity<ApiResponse<InternshipResponse>> updateInternship(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody InternshipRequest request) {
        InternshipResponse response = internshipService.updateInternship(id, principal, request);
        return ResponseEntity.ok(ApiResponse.success("Internship updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "Delete an internship opportunity")
    public ResponseEntity<ApiResponse<Void>> deleteInternship(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        internshipService.deleteInternship(id, principal);
        return ResponseEntity.ok(ApiResponse.success("Internship deleted successfully", null));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('COMPANY') or hasRole('ADMIN')")
    @Operation(summary = "Close an internship opportunity to new applicants")
    public ResponseEntity<ApiResponse<InternshipResponse>> closeInternship(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        InternshipResponse response = internshipService.closeInternship(id, principal);
        return ResponseEntity.ok(ApiResponse.success("Internship marked as closed", response));
    }
}
