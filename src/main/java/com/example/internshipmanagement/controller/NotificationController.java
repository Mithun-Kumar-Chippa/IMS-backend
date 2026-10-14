package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.common.ApiResponse;
import com.example.internshipmanagement.dto.common.PagedResponse;
import com.example.internshipmanagement.dto.notification.NotificationDto;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Endpoints for viewing and updating user in-app notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get paginated notifications for current user")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationDto>>> getNotifications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<NotificationDto> notifications = notificationService.getUserNotifications(principal, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent 20 notifications for current user")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getRecentNotifications(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<NotificationDto> notifications = notificationService.getRecentNotifications(principal);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get total unread notifications count for badge display")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal principal) {
        long count = notificationService.getUnreadCount(principal);
        return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", count)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAsRead(id, principal);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PostMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
