package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.application.ApplicationResponse;
import com.example.internshipmanagement.dto.profile.CompanyProfileDto;
import com.example.internshipmanagement.dto.report.EvaluationDto;
import com.example.internshipmanagement.dto.report.InternshipRecordDto;
import com.example.internshipmanagement.dto.report.ProgressReportDto;
import com.example.internshipmanagement.entity.*;
import com.example.internshipmanagement.enums.ApplicationStatus;
import com.example.internshipmanagement.enums.NotificationType;
import com.example.internshipmanagement.enums.RecordStatus;
import com.example.internshipmanagement.enums.Role;
import com.example.internshipmanagement.exception.ForbiddenException;
import com.example.internshipmanagement.exception.ResourceNotFoundException;
import com.example.internshipmanagement.repository.*;
import com.example.internshipmanagement.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CompanyService {

    private final UserRepository userRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipRecordRepository recordRepository;
    private final ProgressReportRepository progressReportRepository;
    private final EvaluationRepository evaluationRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public CompanyService(UserRepository userRepository,
                          CompanyProfileRepository companyProfileRepository,
                          InternshipApplicationRepository applicationRepository,
                          InternshipRepository internshipRepository,
                          InternshipRecordRepository recordRepository,
                          ProgressReportRepository progressReportRepository,
                          EvaluationRepository evaluationRepository,
                          NotificationService notificationService,
                          AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.recordRepository = recordRepository;
        this.progressReportRepository = progressReportRepository;
        this.evaluationRepository = evaluationRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public CompanyProfileDto getCompanyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        CompanyProfile profile = companyProfileRepository.findByUserId(userId)
                .orElseGet(() -> companyProfileRepository.save(new CompanyProfile(user, user.getFullName())));

        return mapToDto(user, profile);
    }

    @Transactional
    public CompanyProfileDto updateCompanyProfile(Long userId, CompanyProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        userRepository.save(user);

        CompanyProfile profile = companyProfileRepository.findByUserId(userId)
                .orElseGet(() -> new CompanyProfile(user, dto.getCompanyName() != null ? dto.getCompanyName() : user.getFullName()));

        if (dto.getCompanyName() != null && !dto.getCompanyName().isBlank()) {
            profile.setCompanyName(dto.getCompanyName());
        }
        profile.setIndustry(dto.getIndustry());
        profile.setWebsite(dto.getWebsite());
        profile.setLocation(dto.getLocation());
        profile.setDescription(dto.getDescription());
        CompanyProfile saved = companyProfileRepository.save(profile);

        auditLogService.logAction("COMPANY_PROFILE_UPDATE", user.getFullName(), user.getEmail(),
                "Updated company profile", "N/A");

        return mapToDto(user, saved);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getCompanyApplicants(Long companyId, ApplicationStatus status, Pageable pageable) {
        Page<InternshipApplication> page = (status == null)
                ? applicationRepository.findByCompanyId(companyId, pageable)
                : applicationRepository.findByCompanyIdAndStatus(companyId, status, pageable);

        return page.map(this::mapApplicationToDto);
    }

    @Transactional(readOnly = true)
    public Page<InternshipRecordDto> getCompanyInternshipRecords(Long companyId, Pageable pageable) {
        return recordRepository.findByCompanyId(companyId, pageable)
                .map(this::mapRecordToDto);
    }

    @Transactional
    public ProgressReportDto reviewProgressReport(Long reportId, String feedback, UserPrincipal companyPrincipal) {
        ProgressReport report = progressReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress report not found with id: " + reportId));

        if (!report.getInternshipRecord().getCompany().getId().equals(companyPrincipal.getId()) &&
            companyPrincipal.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Unauthorized to review reports for another company's intern.");
        }

        report.setFeedback(feedback);
        report.setReviewedAt(LocalDateTime.now());
        ProgressReport saved = progressReportRepository.save(report);

        // Notify student
        notificationService.sendNotification(
                report.getInternshipRecord().getStudent(),
                "Feedback on Progress Report",
                "Your mentor reviewed Week " + report.getWeekNumber() + " report for " + report.getInternshipRecord().getInternship().getTitle(),
                NotificationType.PROGRESS_REPORT,
                report.getInternshipRecord().getId()
        );

        return mapReportToDto(saved);
    }

    @Transactional
    public EvaluationDto submitEvaluation(Long recordId, EvaluationDto dto, UserPrincipal companyPrincipal) {
        InternshipRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship record not found with id: " + recordId));

        if (!record.getCompany().getId().equals(companyPrincipal.getId()) &&
            companyPrincipal.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Unauthorized to evaluate interns of another company.");
        }

        User evaluator = userRepository.findById(companyPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluator user not found"));

        Evaluation evaluation = evaluationRepository.findByInternshipRecordId(recordId)
                .orElseGet(Evaluation::new);

        evaluation.setInternshipRecord(record);
        evaluation.setEvaluatedBy(evaluator);
        evaluation.setTechnicalSkillRating(dto.getTechnicalSkillRating());
        evaluation.setSoftSkillRating(dto.getSoftSkillRating());
        evaluation.setPunctualityRating(dto.getPunctualityRating());
        evaluation.setOverallRating(dto.getOverallRating());
        evaluation.setComments(dto.getComments());
        evaluation.setRecommendation(dto.getRecommendation());
        Evaluation saved = evaluationRepository.save(evaluation);

        // Mark record completed
        record.setStatus(RecordStatus.COMPLETED);
        record.setFinalRemarks("Completed with rating: " + dto.getOverallRating() + "/5. Recommendation: " + dto.getRecommendation());
        recordRepository.save(record);

        // Notify student
        notificationService.sendNotification(
                record.getStudent(),
                "Internship Evaluation Completed!",
                "Your internship at " + record.getCompany().getFullName() + " has been formally evaluated. Overall rating: " + dto.getOverallRating() + "/5.",
                NotificationType.EVALUATION,
                record.getId()
        );

        auditLogService.logAction("EVALUATION_SUBMIT", companyPrincipal.getFullName(), companyPrincipal.getEmail(),
                "Submitted evaluation for record #" + recordId, "N/A");

        return mapEvaluationToDto(saved);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCompanyDashboardStats(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        long totalPostings = internshipRepository.findByCompanyId(companyId).size();
        long totalApplicants = applicationRepository.countByCompanyId(companyId);
        long pendingReview = applicationRepository.countByCompanyIdAndStatus(companyId, ApplicationStatus.APPLIED);
        long acceptedApplicants = applicationRepository.countByCompanyIdAndStatus(companyId, ApplicationStatus.ACCEPTED);
        long activeInterns = recordRepository.countByCompanyId(companyId);

        stats.put("totalPostings", totalPostings);
        stats.put("totalApplicants", totalApplicants);
        stats.put("pendingReview", pendingReview);
        stats.put("acceptedApplicants", acceptedApplicants);
        stats.put("activeInterns", activeInterns);
        return stats;
    }

    private CompanyProfileDto mapToDto(User user, CompanyProfile profile) {
        CompanyProfileDto dto = new CompanyProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setCompanyName(profile.getCompanyName());
        dto.setIndustry(profile.getIndustry());
        dto.setWebsite(profile.getWebsite());
        dto.setLocation(profile.getLocation());
        dto.setDescription(profile.getDescription());
        dto.setVerified(profile.isVerified());
        dto.setVerificationNotes(profile.getVerificationNotes());
        return dto;
    }

    private ApplicationResponse mapApplicationToDto(InternshipApplication app) {
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

    private EvaluationDto mapEvaluationToDto(Evaluation e) {
        EvaluationDto dto = new EvaluationDto();
        dto.setId(e.getId());
        dto.setRecordId(e.getInternshipRecord().getId());
        dto.setEvaluatorId(e.getEvaluatedBy().getId());
        dto.setEvaluatorName(e.getEvaluatedBy().getFullName());
        dto.setTechnicalSkillRating(e.getTechnicalSkillRating());
        dto.setSoftSkillRating(e.getSoftSkillRating());
        dto.setPunctualityRating(e.getPunctualityRating());
        dto.setOverallRating(e.getOverallRating());
        dto.setComments(e.getComments());
        dto.setRecommendation(e.getRecommendation());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }
}
