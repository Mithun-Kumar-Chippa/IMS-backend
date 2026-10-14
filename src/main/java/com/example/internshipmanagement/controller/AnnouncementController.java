package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.announcement.AnnouncementDto;
import com.example.internshipmanagement.dto.common.ApiResponse;
import com.example.internshipmanagement.enums.Role;
import com.example.internshipmanagement.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/announcements")
@Tag(name = "Announcements", description = "Public announcements endpoint")
public class AnnouncementController {

    private final AdminService adminService;

    public AnnouncementController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/public")
    @Operation(summary = "Get active announcements for public or role landing views")
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getPublicAnnouncements(
            @RequestParam(required = false) Role role) {
        List<AnnouncementDto> announcements = adminService.getPublicAnnouncements(role);
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }
}
