package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.common.PagedResponse;
import com.example.internshipmanagement.dto.internship.InternshipFilterDto;
import com.example.internshipmanagement.dto.internship.InternshipRequest;
import com.example.internshipmanagement.dto.internship.InternshipResponse;
import com.example.internshipmanagement.dto.report.PlatformStatsDto;
import com.example.internshipmanagement.entity.CompanyProfile;
import com.example.internshipmanagement.entity.Internship;
import com.example.internshipmanagement.entity.User;
import com.example.internshipmanagement.enums.ApplicationStatus;
import com.example.internshipmanagement.enums.InternshipStatus;
import com.example.internshipmanagement.enums.Role;
import com.example.internshipmanagement.exception.ForbiddenException;
import com.example.internshipmanagement.exception.ResourceNotFoundException;
import com.example.internshipmanagement.repository.*;
import com.example.internshipmanagement.security.UserPrincipal;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final InternshipRecordRepository recordRepository;
    private final AuditLogService auditLogService;

    public InternshipService(InternshipRepository internshipRepository,
                             UserRepository userRepository,
                             CompanyProfileRepository companyProfileRepository,
                             InternshipApplicationRepository applicationRepository,
                             InternshipRecordRepository recordRepository,
                             AuditLogService auditLogService) {
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.applicationRepository = applicationRepository;
        this.recordRepository = recordRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "internships", key = "T(java.util.Objects).hash(#filter != null ? #filter.getKeyword() : '', #filter != null ? #filter.getLocation() : '', #filter != null ? #filter.getWorkMode() : '', #filter != null ? #filter.getMinStipend() : 0, #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString())")
    public PagedResponse<InternshipResponse> searchInternships(InternshipFilterDto filter, Pageable pageable) {
        String keyword = (filter != null && filter.getKeyword() != null && !filter.getKeyword().isBlank()) ? filter.getKeyword().trim() : null;
        String location = (filter != null && filter.getLocation() != null && !filter.getLocation().isBlank()) ? filter.getLocation().trim() : null;
        var workMode = (filter != null) ? filter.getWorkMode() : null;
        Double minStipend = (filter != null) ? filter.getMinStipend() : null;
        boolean activeOnly = (filter == null || filter.isActiveOnly());

        Page<Internship> page = internshipRepository.searchInternships(
                InternshipStatus.APPROVED,
                keyword,
                workMode,
                location,
                minStipend,
                activeOnly,
                LocalDate.now(),
                pageable
        );

        List<InternshipResponse> responses = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Transactional(readOnly = true)
    public InternshipResponse getInternshipById(Long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship opportunity not found with id: " + id));

        return mapToResponse(internship);
    }

    @Transactional
    @CacheEvict(value = {"internships", "internship-stats", "platform-summary"}, allEntries = true)
    public InternshipResponse createInternship(UserPrincipal companyPrincipal, InternshipRequest request) {
        User company = userRepository.findById(companyPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company user not found"));

        Internship internship = new Internship();
        internship.setCompany(company);
        internship.setTitle(request.getTitle());
        internship.setDescription(request.getDescription());
        internship.setDepartment(request.getDepartment());
        internship.setLocation(request.getLocation());
        internship.setWorkMode(request.getWorkMode());
        internship.setStipend(request.getStipend());
        internship.setDurationWeeks(request.getDurationWeeks());
        internship.setRequirements(request.getRequirements());
        internship.setResponsibilities(request.getResponsibilities());
        internship.setDeadline(request.getDeadline());
        internship.setMaxPositions(request.getMaxPositions() != null ? request.getMaxPositions() : 1);

        // Check if company is verified by admin
        boolean isCompanyVerified = companyProfileRepository.findByUserId(company.getId())
                .map(CompanyProfile::isVerified)
                .orElse(false);

        // If company is already verified, postings can be directly approved, otherwise requires admin approval
        internship.setStatus(isCompanyVerified ? InternshipStatus.APPROVED : InternshipStatus.PENDING_APPROVAL);

        Internship saved = internshipRepository.save(internship);

        auditLogService.logAction("CREATE_INTERNSHIP", company.getFullName(), company.getEmail(),
                "Created internship posting: " + saved.getTitle(), "N/A");

        return mapToResponse(saved);
    }

    @Transactional
    @CacheEvict(value = {"internships", "internship-stats", "platform-summary"}, allEntries = true)
    public InternshipResponse updateInternship(Long id, UserPrincipal companyPrincipal, InternshipRequest request) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with id: " + id));

        if (!internship.getCompany().getId().equals(companyPrincipal.getId()) &&
            companyPrincipal.getRole() != Role.ADMIN) {
            throw new ForbiddenException("You are not authorized to update another company's internship posting.");
        }

        internship.setTitle(request.getTitle());
        internship.setDescription(request.getDescription());
        internship.setDepartment(request.getDepartment());
        internship.setLocation(request.getLocation());
        internship.setWorkMode(request.getWorkMode());
        internship.setStipend(request.getStipend());
        internship.setDurationWeeks(request.getDurationWeeks());
        internship.setRequirements(request.getRequirements());
        internship.setResponsibilities(request.getResponsibilities());
        internship.setDeadline(request.getDeadline());
        if (request.getMaxPositions() != null) {
            internship.setMaxPositions(request.getMaxPositions());
        }

        Internship updated = internshipRepository.save(internship);

        auditLogService.logAction("UPDATE_INTERNSHIP", companyPrincipal.getFullName(), companyPrincipal.getEmail(),
                "Updated internship posting #" + id + ": " + updated.getTitle(), "N/A");

        return mapToResponse(updated);
    }

    @Transactional
    @CacheEvict(value = {"internships", "internship-stats", "platform-summary"}, allEntries = true)
    public void deleteInternship(Long id, UserPrincipal companyPrincipal) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with id: " + id));

        if (!internship.getCompany().getId().equals(companyPrincipal.getId()) &&
            companyPrincipal.getRole() != Role.ADMIN) {
            throw new ForbiddenException("You are not authorized to delete another company's internship posting.");
        }

        internshipRepository.delete(internship);

        auditLogService.logAction("DELETE_INTERNSHIP", companyPrincipal.getFullName(), companyPrincipal.getEmail(),
                "Deleted internship posting #" + id, "N/A");
    }

    @Transactional
    @CacheEvict(value = {"internships", "internship-stats", "platform-summary"}, allEntries = true)
    public InternshipResponse closeInternship(Long id, UserPrincipal companyPrincipal) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with id: " + id));

        if (!internship.getCompany().getId().equals(companyPrincipal.getId()) &&
            companyPrincipal.getRole() != Role.ADMIN) {
            throw new ForbiddenException("You are not authorized to close another company's internship posting.");
        }

        internship.setStatus(InternshipStatus.CLOSED);
        Internship saved = internshipRepository.save(internship);

        auditLogService.logAction("CLOSE_INTERNSHIP", companyPrincipal.getFullName(), companyPrincipal.getEmail(),
                "Closed internship posting #" + id, "N/A");

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "platform-summary")
    public PlatformStatsDto getPlatformStats() {
        long totalInternships = internshipRepository.count();
        long activeCompanies = companyProfileRepository.countByVerifiedTrue();
        long registeredStudents = userRepository.countByRole(Role.STUDENT);
        long studentsPlaced = applicationRepository.countByStatus(ApplicationStatus.ACCEPTED);

        return new PlatformStatsDto(totalInternships, activeCompanies, registeredStudents, studentsPlaced);
    }

    private InternshipResponse mapToResponse(Internship i) {
        InternshipResponse resp = new InternshipResponse();
        resp.setId(i.getId());
        resp.setCompanyId(i.getCompany().getId());
        resp.setCompanyName(i.getCompany().getFullName());

        CompanyProfile cp = i.getCompany().getCompanyProfile();
        if (cp != null) {
            resp.setCompanyLocation(cp.getLocation());
            if (cp.getCompanyName() != null) {
                resp.setCompanyName(cp.getCompanyName());
            }
        }

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
}
