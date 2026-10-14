package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.announcement.AnnouncementDto;
import com.example.internshipmanagement.dto.auth.UserSummaryDto;
import com.example.internshipmanagement.dto.common.PagedResponse;
import com.example.internshipmanagement.dto.internship.InternshipResponse;
import com.example.internshipmanagement.dto.profile.CompanyProfileDto;
import com.example.internshipmanagement.dto.report.AdminDashboardMetricsDto;
import com.example.internshipmanagement.entity.*;
import com.example.internshipmanagement.enums.ApplicationStatus;
import com.example.internshipmanagement.enums.InternshipStatus;
import com.example.internshipmanagement.enums.NotificationType;
import com.example.internshipmanagement.enums.Role;
import com.example.internshipmanagement.exception.ResourceNotFoundException;
import com.example.internshipmanagement.repository.*;
import com.example.internshipmanagement.security.UserPrincipal;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final AnnouncementRepository announcementRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public AdminService(UserRepository userRepository,
                        CompanyProfileRepository companyProfileRepository,
                        InternshipRepository internshipRepository,
                        InternshipApplicationRepository applicationRepository,
                        AnnouncementRepository announcementRepository,
                        NotificationService notificationService,
                        AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        this.announcementRepository = announcementRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "platform-summary")
    public AdminDashboardMetricsDto getDashboardMetrics() {
        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long totalCompanies = userRepository.countByRole(Role.COMPANY);
        long totalInternships = internshipRepository.count();
        long totalApplications = applicationRepository.count();
        long pendingVerifications = companyProfileRepository.countByVerifiedFalse();
        long pendingApprovals = internshipRepository.countByStatus(InternshipStatus.PENDING_APPROVAL);
        long acceptedApplications = applicationRepository.countByStatus(ApplicationStatus.ACCEPTED);
        long activeUsers = userRepository.countByActiveTrue();

        return new AdminDashboardMetricsDto(
                totalStudents,
                totalCompanies,
                totalInternships,
                totalApplications,
                pendingVerifications,
                pendingApprovals,
                acceptedApplications,
                activeUsers
        );
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserSummaryDto> getAllUsers(Role role, Pageable pageable) {
        Page<User> page = (role == null)
                ? userRepository.findAll(pageable)
                : userRepository.findByRole(role, pageable);

        List<UserSummaryDto> dtos = page.getContent().stream()
                .map(u -> new UserSummaryDto(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(), u.getRole(), u.isActive(), u.getCreatedAt()))
                .collect(Collectors.toList());

        return new PagedResponse<>(dtos, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional
    public void setUserActiveStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setActive(active);
        userRepository.save(user);

        auditLogService.logAction("USER_STATUS_CHANGE", "ADMIN", user.getEmail(),
                "Set user active status to: " + active, "N/A");
    }

    @Transactional(readOnly = true)
    public PagedResponse<CompanyProfileDto> getPendingCompanies(Pageable pageable) {
        Page<CompanyProfile> page = companyProfileRepository.findByVerifiedFalse(pageable);
        List<CompanyProfileDto> dtos = page.getContent().stream()
                .map(cp -> {
                    CompanyProfileDto dto = new CompanyProfileDto();
                    dto.setId(cp.getId());
                    dto.setUserId(cp.getUser().getId());
                    dto.setEmail(cp.getUser().getEmail());
                    dto.setFullName(cp.getUser().getFullName());
                    dto.setPhone(cp.getUser().getPhone());
                    dto.setCompanyName(cp.getCompanyName());
                    dto.setIndustry(cp.getIndustry());
                    dto.setWebsite(cp.getWebsite());
                    dto.setLocation(cp.getLocation());
                    dto.setDescription(cp.getDescription());
                    dto.setVerified(cp.isVerified());
                    dto.setVerificationNotes(cp.getVerificationNotes());
                    return dto;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(dtos, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional
    @CacheEvict(value = "platform-summary", allEntries = true)
    public void verifyCompany(Long companyId, boolean verify, String notes) {
        CompanyProfile profile = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found with id: " + companyId));

        profile.setVerified(verify);
        profile.setVerificationNotes(notes);
        companyProfileRepository.save(profile);

        // Notify company user
        notificationService.sendNotification(
                profile.getUser(),
                verify ? "Company Profile Verified!" : "Company Verification Rejected",
                verify ? "Your company profile has been verified by the administrator. You can now post internships freely."
                       : "Your company verification request was not approved: " + notes,
                NotificationType.COMPANY_VERIFICATION,
                profile.getId()
        );

        auditLogService.logAction("COMPANY_VERIFICATION", "ADMIN", profile.getUser().getEmail(),
                "Set verification status to " + verify + " for " + profile.getCompanyName(), "N/A");
    }

    @Transactional(readOnly = true)
    public PagedResponse<InternshipResponse> getPendingInternships(Pageable pageable) {
        Page<Internship> page = internshipRepository.findByStatus(InternshipStatus.PENDING_APPROVAL, pageable);
        List<InternshipResponse> dtos = page.getContent().stream()
                .map(this::mapInternshipToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(dtos, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional
    @CacheEvict(value = {"internships", "internship-stats", "platform-summary"}, allEntries = true)
    public void approveInternship(Long internshipId, boolean approve, String notes) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with id: " + internshipId));

        internship.setStatus(approve ? InternshipStatus.APPROVED : InternshipStatus.REJECTED);
        internshipRepository.save(internship);

        // Notify company
        notificationService.sendNotification(
                internship.getCompany(),
                approve ? "Internship Posting Approved" : "Internship Posting Rejected",
                approve ? "Your internship posting '" + internship.getTitle() + "' has been approved and is now live for students to apply."
                        : "Your internship posting '" + internship.getTitle() + "' was rejected. Reason: " + notes,
                NotificationType.INTERNSHIP_APPROVAL,
                internship.getId()
        );

        auditLogService.logAction("INTERNSHIP_APPROVAL", "ADMIN", internship.getCompany().getEmail(),
                "Set approval to " + approve + " for internship #" + internshipId, "N/A");
    }

    @Transactional
    public AnnouncementDto createAnnouncement(UserPrincipal adminPrincipal, AnnouncementDto dto) {
        User admin = userRepository.findById(adminPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        Announcement announcement = new Announcement(dto.getTitle(), dto.getContent(), dto.getTargetRole(), admin);
        Announcement saved = announcementRepository.save(announcement);

        auditLogService.logAction("CREATE_ANNOUNCEMENT", admin.getFullName(), admin.getEmail(),
                "Created system announcement: " + dto.getTitle(), "N/A");

        return mapAnnouncementToDto(saved);
    }

    @Transactional(readOnly = true)
    public PagedResponse<AnnouncementDto> getAllAnnouncements(Pageable pageable) {
        Page<Announcement> page = announcementRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<AnnouncementDto> dtos = page.getContent().stream()
                .map(this::mapAnnouncementToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(dtos, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDto> getPublicAnnouncements(Role role) {
        List<Announcement> list = (role == null)
                ? announcementRepository.findByActiveTrueOrderByCreatedAtDesc()
                : announcementRepository.findActiveByRole(role);

        return list.stream()
                .map(this::mapAnnouncementToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    private InternshipResponse mapInternshipToResponse(Internship i) {
        InternshipResponse resp = new InternshipResponse();
        resp.setId(i.getId());
        resp.setCompanyId(i.getCompany().getId());
        resp.setCompanyName(i.getCompany().getFullName());
        resp.setTitle(i.getTitle());
        resp.setDescription(i.getDescription());
        resp.setDepartment(i.getDepartment());
        resp.setLocation(i.getLocation());
        resp.setWorkMode(i.getWorkMode());
        resp.setStipend(i.getStipend());
        resp.setDurationWeeks(i.getDurationWeeks());
        resp.setRequirements(i.getRequirements());
        resp.setResponsibilities(i.getResponsibilities());
        resp.setDeadline(i.getDeadline());
        resp.setStatus(i.getStatus());
        resp.setMaxPositions(i.getMaxPositions());
        resp.setApplicationsCount(i.getApplications() != null ? i.getApplications().size() : 0);
        resp.setCreatedAt(i.getCreatedAt());
        resp.setUpdatedAt(i.getUpdatedAt());
        return resp;
    }

    private AnnouncementDto mapAnnouncementToDto(Announcement a) {
        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(a.getId());
        dto.setTitle(a.getTitle());
        dto.setContent(a.getContent());
        dto.setTargetRole(a.getTargetRole());
        dto.setAuthorName(a.getAuthor() != null ? a.getAuthor().getFullName() : "Administrator");
        dto.setActive(a.isActive());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}
