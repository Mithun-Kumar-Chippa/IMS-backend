package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.common.ApiResponse;
import com.example.internshipmanagement.dto.profile.StudentProfileDto;
import com.example.internshipmanagement.dto.report.InternshipRecordDto;
import com.example.internshipmanagement.dto.report.ProgressReportDto;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Student Operations", description = "Endpoints for student profile, resume uploads, records, and weekly progress reports")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Get current student profile details")
    public ResponseEntity<ApiResponse<StudentProfileDto>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        StudentProfileDto profile = studentService.getStudentProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @Operation(summary = "Update student profile details")
    public ResponseEntity<ApiResponse<StudentProfileDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody StudentProfileDto dto) {
        StudentProfileDto updated = studentService.updateStudentProfile(principal.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Upload PDF resume for student profile")
    public ResponseEntity<ApiResponse<StudentProfileDto>> uploadResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        StudentProfileDto updated = studentService.uploadResume(principal.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", updated));
    }

    @GetMapping("/records")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get all enrolled internship records for current student")
    public ResponseEntity<ApiResponse<Page<InternshipRecordDto>>> getMyRecords(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InternshipRecordDto> records = studentService.getAcceptedInternshipRecords(principal, pageable);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @PostMapping("/records/{recordId}/reports")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit a weekly progress report for an active internship")
    public ResponseEntity<ApiResponse<ProgressReportDto>> submitReport(
            @PathVariable Long recordId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProgressReportDto dto) {
        ProgressReportDto saved = studentService.submitProgressReport(principal, recordId, dto);
        return ResponseEntity.ok(ApiResponse.success("Progress report submitted successfully", saved));
    }

    @GetMapping("/records/{recordId}/reports")
    @Operation(summary = "Get all progress reports for an internship record")
    public ResponseEntity<ApiResponse<List<ProgressReportDto>>> getRecordReports(
            @PathVariable Long recordId,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ProgressReportDto> reports = studentService.getRecordProgressReports(recordId, principal);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
}
