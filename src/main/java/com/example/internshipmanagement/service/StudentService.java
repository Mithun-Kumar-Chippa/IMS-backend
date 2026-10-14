package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.profile.StudentProfileDto;
import com.example.internshipmanagement.dto.report.InternshipRecordDto;
import com.example.internshipmanagement.dto.report.ProgressReportDto;
import com.example.internshipmanagement.entity.*;
import com.example.internshipmanagement.enums.NotificationType;
import com.example.internshipmanagement.enums.Role;
import com.example.internshipmanagement.exception.BadRequestException;
import com.example.internshipmanagement.exception.ForbiddenException;
import com.example.internshipmanagement.exception.ResourceNotFoundException;
import com.example.internshipmanagement.repository.*;
import com.example.internshipmanagement.security.UserPrincipal;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final InternshipRecordRepository recordRepository;
    private final ProgressReportRepository progressReportRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public StudentService(UserRepository userRepository,
                          StudentProfileRepository studentProfileRepository,
                          InternshipApplicationRepository applicationRepository,
                          InternshipRecordRepository recordRepository,
                          ProgressReportRepository progressReportRepository,
                          FileStorageService fileStorageService,
                          NotificationService notificationService,
                          AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.applicationRepository = applicationRepository;
        this.recordRepository = recordRepository;
        this.progressReportRepository = progressReportRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public StudentProfileDto getStudentProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseGet(() -> studentProfileRepository.save(new StudentProfile(user)));

        return mapToDto(user, profile);
    }

    @Transactional
    public StudentProfileDto updateStudentProfile(Long userId, StudentProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        userRepository.save(user);

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseGet(() -> new StudentProfile(user));

        profile.setCollegeName(dto.getCollegeName());
        profile.setDegree(dto.getDegree());
        profile.setDepartment(dto.getDepartment());
        profile.setGraduationYear(dto.getGraduationYear());
        profile.setGpa(dto.getGpa());
        profile.setSkills(dto.getSkills());
        profile.setCareerInterests(dto.getCareerInterests());
        StudentProfile saved = studentProfileRepository.save(profile);

        auditLogService.logAction("PROFILE_UPDATE", user.getFullName(), user.getEmail(),
                "Updated student profile", "N/A");

        return mapToDto(user, saved);
    }

    @Transactional
    public StudentProfileDto uploadResume(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseGet(() -> new StudentProfile(user));

        String storedFileName = fileStorageService.storeFile(file);
        String fileDownloadUri = "/api/v1/files/download/" + storedFileName;

        profile.setResumeFileName(file.getOriginalFilename());
        profile.setResumeUrl(fileDownloadUri);
        StudentProfile saved = studentProfileRepository.save(profile);

        auditLogService.logAction("RESUME_UPLOAD", user.getFullName(), user.getEmail(),
                "Uploaded resume: " + file.getOriginalFilename(), "N/A");

        return mapToDto(user, saved);
    }

    @Transactional(readOnly = true)
    public Resource downloadResume(String fileName, UserPrincipal principal) {
        // Find if this resume file belongs to a student
        StudentProfile profile = studentProfileRepository.findAll().stream()
                .filter(p -> p.getResumeUrl() != null && p.getResumeUrl().endsWith(fileName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Resume record not found for file: " + fileName));

        Long studentId = profile.getUser().getId();

        // Enforce ownership and access authorization
        if (principal.getRole() == Role.ADMIN) {
            // Admin allowed
        } else if (principal.getId().equals(studentId)) {
            // Student themselves allowed
        } else if (principal.getRole() == Role.COMPANY) {
            // Allowed ONLY if the student applied to this company's postings
            boolean hasAppliedToCompany = applicationRepository.findByCompanyId(principal.getId(), Pageable.unpaged())
                    .getContent().stream()
                    .anyMatch(app -> app.getStudent().getId().equals(studentId));

            if (!hasAppliedToCompany) {
                throw new ForbiddenException("You are not authorized to view this student's resume as no application was submitted to your company.");
            }
        } else {
            throw new ForbiddenException("Unauthorized to access this resume.");
        }

        return fileStorageService.loadFileAsResource(fileName);
    }

    @Transactional(readOnly = true)
    public Page<InternshipRecordDto> getAcceptedInternshipRecords(UserPrincipal studentPrincipal, Pageable pageable) {
        return recordRepository.findByStudentId(studentPrincipal.getId(), pageable)
                .map(this::mapRecordToDto);
    }

    @Transactional
    public ProgressReportDto submitProgressReport(UserPrincipal studentPrincipal, Long recordId, ProgressReportDto dto) {
        InternshipRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship record not found with id: " + recordId));

        if (!record.getStudent().getId().equals(studentPrincipal.getId())) {
            throw new ForbiddenException("You can only submit progress reports for your own active internships.");
        }

        ProgressReport report = new ProgressReport();
        report.setInternshipRecord(record);
        report.setWeekNumber(dto.getWeekNumber());
        report.setReportTitle(dto.getReportTitle());
        report.setTasksCompleted(dto.getTasksCompleted());
        report.setChallengesFaced(dto.getChallengesFaced());
        report.setPlansForNextWeek(dto.getPlansForNextWeek());
        report.setHoursWorked(dto.getHoursWorked());
        ProgressReport saved = progressReportRepository.save(report);

        // Notify company mentor
        notificationService.sendNotification(
                record.getCompany(),
                "New Progress Report Submitted",
                "Student " + record.getStudent().getFullName() + " submitted Week " + dto.getWeekNumber() + " report for " + record.getInternship().getTitle(),
                NotificationType.PROGRESS_REPORT,
                record.getId()
        );

        auditLogService.logAction("PROGRESS_REPORT_SUBMIT", studentPrincipal.getFullName(), studentPrincipal.getEmail(),
                "Submitted week " + dto.getWeekNumber() + " report for record #" + recordId, "N/A");

        return mapReportToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ProgressReportDto> getRecordProgressReports(Long recordId, UserPrincipal principal) {
        InternshipRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship record not found with id: " + recordId));

        if (!record.getStudent().getId().equals(principal.getId()) &&
            !record.getCompany().getId().equals(principal.getId()) &&
            principal.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Unauthorized to view reports for this internship record.");
        }

        return progressReportRepository.findByInternshipRecordIdOrderByWeekNumberAsc(recordId)
                .stream()
                .map(this::mapReportToDto)
                .collect(Collectors.toList());
    }

    private StudentProfileDto mapToDto(User user, StudentProfile profile) {
        StudentProfileDto dto = new StudentProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setCollegeName(profile.getCollegeName());
        dto.setDegree(profile.getDegree());
        dto.setDepartment(profile.getDepartment());
        dto.setGraduationYear(profile.getGraduationYear());
        dto.setGpa(profile.getGpa());
        dto.setSkills(profile.getSkills());
        dto.setCareerInterests(profile.getCareerInterests());
        dto.setResumeUrl(profile.getResumeUrl());
        dto.setResumeFileName(profile.getResumeFileName());
        return dto;
    }

    private InternshipRecordDto mapRecordToDto(InternshipRecord r) {
        InternshipRecordDto dto = new InternshipRecordDto();
        dto.setId(r.getId());
        dto.setApplicationId(r.getApplication().getId());
        dto.setStudentId(r.getStudent().getId());
        dto.setStudentName(r.getStudent().getFullName());
        dto.setStudentEmail(r.getStudent().getEmail());
        dto.setCompanyId(r.getCompany().getId());
        dto.setCompanyName(r.getCompany().getFullName());
        dto.setInternshipId(r.getInternship().getId());
        dto.setInternshipTitle(r.getInternship().getTitle());
        dto.setMentorName(r.getMentorName());
        dto.setMentorEmail(r.getMentorEmail());
        dto.setStartDate(r.getStartDate());
        dto.setEndDate(r.getEndDate());
        dto.setStatus(r.getStatus());
        dto.setFinalRemarks(r.getFinalRemarks());
        dto.setProgressReportsCount(r.getProgressReports() != null ? r.getProgressReports().size() : 0);
        dto.setEvaluated(r.getEvaluation() != null);
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    private ProgressReportDto mapReportToDto(ProgressReport p) {
        ProgressReportDto dto = new ProgressReportDto();
        dto.setId(p.getId());
        dto.setRecordId(p.getInternshipRecord().getId());
        dto.setWeekNumber(p.getWeekNumber());
        dto.setReportTitle(p.getReportTitle());
        dto.setTasksCompleted(p.getTasksCompleted());
        dto.setChallengesFaced(p.getChallengesFaced());
        dto.setPlansForNextWeek(p.getPlansForNextWeek());
        dto.setHoursWorked(p.getHoursWorked());
        dto.setSubmittedAt(p.getSubmittedAt());
        dto.setFeedback(p.getFeedback());
        dto.setReviewedAt(p.getReviewedAt());
        return dto;
    }
}
