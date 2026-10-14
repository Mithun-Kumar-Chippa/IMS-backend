package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.application.ApplicationRequest;
import com.example.internshipmanagement.dto.application.ApplicationResponse;
import com.example.internshipmanagement.dto.application.StatusUpdateRequest;
import com.example.internshipmanagement.dto.common.PagedResponse;
import com.example.internshipmanagement.entity.*;
import com.example.internshipmanagement.enums.ApplicationStatus;
import com.example.internshipmanagement.enums.InternshipStatus;
import com.example.internshipmanagement.enums.NotificationType;
import com.example.internshipmanagement.enums.RecordStatus;
import com.example.internshipmanagement.enums.Role;
import com.example.internshipmanagement.exception.BadRequestException;
import com.example.internshipmanagement.exception.DuplicateResourceException;
import com.example.internshipmanagement.exception.ForbiddenException;
import com.example.internshipmanagement.exception.ResourceNotFoundException;
import com.example.internshipmanagement.repository.*;
import com.example.internshipmanagement.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final InternshipApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final InternshipRecordRepository recordRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public ApplicationService(InternshipApplicationRepository applicationRepository,
                              InternshipRepository internshipRepository,
                              UserRepository userRepository,
                              StudentProfileRepository studentProfileRepository,
                              InternshipRecordRepository recordRepository,
                              NotificationService notificationService,
                              AuditLogService auditLogService) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recordRepository = recordRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public ApplicationResponse applyForInternship(UserPrincipal studentPrincipal, ApplicationRequest request) {
        User student = userRepository.findById(studentPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Only registered students can apply for internships.");
        }

        Internship internship = internshipRepository.findById(request.getInternshipId())
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with id: " + request.getInternshipId()));

        if (internship.getStatus() != InternshipStatus.APPROVED) {
            throw new BadRequestException("This internship is currently not accepting applications (Status: " + internship.getStatus() + ").");
        }

        if (internship.getDeadline() != null && internship.getDeadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("Application deadline for this internship passed on " + internship.getDeadline() + ".");
        }

        if (applicationRepository.existsByInternshipIdAndStudentId(internship.getId(), student.getId())) {
            throw new DuplicateResourceException("You have already submitted an application for this internship.");
        }

        String resumeUrl = request.getResumeUrl();
        if (resumeUrl == null || resumeUrl.isBlank()) {
            StudentProfile profile = studentProfileRepository.findByUserId(student.getId()).orElse(null);
            if (profile != null && profile.getResumeUrl() != null) {
                resumeUrl = profile.getResumeUrl();
            }
        }

        InternshipApplication application = new InternshipApplication();
        application.setInternship(internship);
        application.setStudent(student);
        application.setCoverLetter(request.getCoverLetter());
        application.setResumeUrl(resumeUrl);
        application.setStatus(ApplicationStatus.APPLIED);

        InternshipApplication saved = applicationRepository.save(application);

        // Send notification to company
        notificationService.sendNotification(
                internship.getCompany(),
                "New Internship Application",
                "Student " + student.getFullName() + " applied for " + internship.getTitle(),
                NotificationType.NEW_APPLICATION,
                saved.getId()
        );

        auditLogService.logAction("APPLICATION_SUBMIT", student.getFullName(), student.getEmail(),
                "Applied for internship #" + internship.getId() + " (" + internship.getTitle() + ")", "N/A");

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ApplicationResponse> getStudentApplications(UserPrincipal studentPrincipal, Pageable pageable) {
        Page<InternshipApplication> page = applicationRepository.findByStudentId(studentPrincipal.getId(), pageable);
        List<ApplicationResponse> responses = page.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(Long id, UserPrincipal principal) {
        InternshipApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        boolean isStudentOwner = application.getStudent().getId().equals(principal.getId());
        boolean isCompanyOwner = application.getInternship().getCompany().getId().equals(principal.getId());
        boolean isAdmin = principal.getRole() == Role.ADMIN;

        if (!isStudentOwner && !isCompanyOwner && !isAdmin) {
            throw new ForbiddenException("Unauthorized to access this application.");
        }

        return mapToDto(application);
    }

    @Transactional
    public ApplicationResponse updateApplicationStatus(Long id, UserPrincipal companyPrincipal, StatusUpdateRequest request) {
        InternshipApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        Internship internship = application.getInternship();

        if (!internship.getCompany().getId().equals(companyPrincipal.getId()) &&
            companyPrincipal.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Unauthorized to update applications for another company's posting.");
        }

        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new BadRequestException("Cannot update an application that has already been withdrawn by the student.");
        }

        ApplicationStatus newStatus = request.getStatus();

        // Enforce state transition rules
        if (application.getStatus() == ApplicationStatus.REJECTED && newStatus != ApplicationStatus.UNDER_REVIEW) {
            throw new BadRequestException("Rejected applications can only be moved back to UNDER_REVIEW.");
        }

        application.setStatus(newStatus);
        application.setReviewedAt(LocalDateTime.now());
        if (request.getReviewerNotes() != null) {
            application.setReviewerNotes(request.getReviewerNotes());
        }
        if (request.getMentorName() != null) {
            application.setMentorName(request.getMentorName());
        }

        InternshipApplication saved = applicationRepository.save(application);

        // If ACCEPTED, automatically create InternshipRecord if not exists
        if (newStatus == ApplicationStatus.ACCEPTED) {
            if (recordRepository.findByApplicationId(saved.getId()).isEmpty()) {
                InternshipRecord record = new InternshipRecord();
                record.setApplication(saved);
                record.setStudent(saved.getStudent());
                record.setCompany(internship.getCompany());
                record.setInternship(internship);
                record.setMentorName(request.getMentorName() != null ? request.getMentorName() : internship.getCompany().getFullName());
                record.setMentorEmail(request.getMentorEmail() != null ? request.getMentorEmail() : internship.getCompany().getEmail());
                record.setStartDate(LocalDate.now());
                record.setEndDate(LocalDate.now().plusWeeks(internship.getDurationWeeks() != null ? internship.getDurationWeeks() : 12));
                record.setStatus(RecordStatus.IN_PROGRESS);
                recordRepository.save(record);
            }

            notificationService.sendNotification(
                    saved.getStudent(),
                    "Congratulations! Application Accepted",
                    "Your application for '" + internship.getTitle() + "' at " + internship.getCompany().getFullName() + " has been accepted! Mentor: " + (request.getMentorName() != null ? request.getMentorName() : "Assigned"),
                    NotificationType.APPLICATION_STATUS,
                    saved.getId()
            );
        } else {
            notificationService.sendNotification(
                    saved.getStudent(),
                    "Application Status Updated",
                    "Your application for '" + internship.getTitle() + "' status changed to: " + newStatus,
                    NotificationType.APPLICATION_STATUS,
                    saved.getId()
            );
        }

        auditLogService.logAction("STATUS_UPDATE", companyPrincipal.getFullName(), companyPrincipal.getEmail(),
                "Updated application #" + id + " status to " + newStatus, "N/A");

        return mapToDto(saved);
    }

    @Transactional
    public ApplicationResponse withdrawApplication(Long id, UserPrincipal studentPrincipal) {
        InternshipApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        if (!application.getStudent().getId().equals(studentPrincipal.getId())) {
            throw new ForbiddenException("You can only withdraw your own applications.");
        }

        if (application.getStatus() == ApplicationStatus.ACCEPTED) {
            throw new BadRequestException("Accepted applications cannot be withdrawn directly. Please contact the company or coordinator.");
        }

        if (application.getStatus() == ApplicationStatus.REJECTED || application.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new BadRequestException("Application is already " + application.getStatus() + ".");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        application.setReviewerNotes("Withdrawn by student on " + LocalDate.now());
        InternshipApplication saved = applicationRepository.save(application);

        // Notify company
        notificationService.sendNotification(
                application.getInternship().getCompany(),
                "Application Withdrawn",
                "Student " + application.getStudent().getFullName() + " withdrew application for " + application.getInternship().getTitle(),
                NotificationType.APPLICATION_STATUS,
                saved.getId()
        );

        auditLogService.logAction("APPLICATION_WITHDRAW", studentPrincipal.getFullName(), studentPrincipal.getEmail(),
                "Withdrew application #" + id, "N/A");

        return mapToDto(saved);
    }

    private ApplicationResponse mapToDto(InternshipApplication app) {
        ApplicationResponse dto = new ApplicationResponse();
        dto.setId(app.getId());
        dto.setInternshipId(app.getInternship().getId());
        dto.setInternshipTitle(app.getInternship().getTitle());
        dto.setCompanyId(app.getInternship().getCompany().getId());
        dto.setCompanyName(app.getInternship().getCompany().getFullName());
        dto.setStudentId(app.getStudent().getId());
        dto.setStudentName(app.getStudent().getFullName());
        dto.setStudentEmail(app.getStudent().getEmail());
        dto.setStudentPhone(app.getStudent().getPhone());

        StudentProfile sp = app.getStudent().getStudentProfile();
        if (sp != null) {
            dto.setStudentCollege(sp.getCollegeName());
            dto.setStudentSkills(sp.getSkills());
        }

        dto.setCoverLetter(app.getCoverLetter());
        dto.setResumeUrl(app.getResumeUrl());
        dto.setStatus(app.getStatus());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setReviewedAt(app.getReviewedAt());
        dto.setReviewerNotes(app.getReviewerNotes());
        dto.setMentorName(app.getMentorName());
        if (app.getInternshipRecord() != null) {
            dto.setRecordId(app.getInternshipRecord().getId());
        }
        return dto;
    }
}
