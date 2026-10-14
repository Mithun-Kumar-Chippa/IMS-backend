package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File Storage", description = "Endpoints for downloading uploaded resume documents with security authorization checks")
public class FileController {

    private final StudentService studentService;

    public FileController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/download/{fileName}")
    @Operation(summary = "Download a student resume document (requires authenticated access rights)")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName,
            @AuthenticationPrincipal UserPrincipal principal) {

        // Principal can be null if accessed publicly or we enforce authentication
        Resource resource = studentService.downloadResume(fileName, principal);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
