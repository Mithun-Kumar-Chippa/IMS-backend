package com.example.internshipmanagement;

import com.example.internshipmanagement.dto.application.ApplicationRequest;
import com.example.internshipmanagement.dto.application.StatusUpdateRequest;
import com.example.internshipmanagement.entity.Internship;
import com.example.internshipmanagement.entity.InternshipApplication;
import com.example.internshipmanagement.entity.User;
import com.example.internshipmanagement.enums.ApplicationStatus;
import com.example.internshipmanagement.enums.Role;
import com.example.internshipmanagement.exception.DuplicateResourceException;
import com.example.internshipmanagement.repository.InternshipApplicationRepository;
import com.example.internshipmanagement.repository.InternshipRepository;
import com.example.internshipmanagement.repository.UserRepository;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.ApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ApplicationServiceTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private InternshipApplicationRepository applicationRepository;

    @Test
    @DisplayName("Should prevent duplicate application submission by same student")
    void testDuplicateApplicationPrevention() {
        User alex = userRepository.findByEmail("student@ims.edu")
                .orElseThrow(() -> new IllegalStateException("Seeded student not found"));

        UserPrincipal studentAlex = new UserPrincipal(
                alex.getId(),
                alex.getEmail(),
                alex.getPassword(),
                alex.getFullName(),
                alex.getRole(),
                alex.isActive(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );

        // Find the application seeded for Alex
        InternshipApplication existingApp = applicationRepository.findByStudentId(alex.getId())
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Seeded application not found"));

        ApplicationRequest request = new ApplicationRequest();
        request.setInternshipId(existingApp.getInternship().getId());
        request.setCoverLetter("Trying to apply again");

        assertThrows(DuplicateResourceException.class, () -> {
            applicationService.applyForInternship(studentAlex, request);
        });
    }

    @Test
    @DisplayName("Should update application status from company side")
    void testUpdateApplicationStatus() {
        User company = userRepository.findByEmail("techcorp@example.com")
                .orElseThrow(() -> new IllegalStateException("Seeded company not found"));

        UserPrincipal companyPrincipal = new UserPrincipal(
                company.getId(),
                company.getEmail(),
                company.getPassword(),
                company.getFullName(),
                company.getRole(),
                company.isActive(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_COMPANY"))
        );

        // Find an application for this company's postings
        InternshipApplication app = applicationRepository.findByCompanyId(company.getId(), org.springframework.data.domain.Pageable.unpaged())
                .getContent().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED || a.getStatus() == ApplicationStatus.APPLIED)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Applicable application not found"));

        StatusUpdateRequest update = new StatusUpdateRequest(ApplicationStatus.UNDER_REVIEW, "Re-checking test scores");
        var updated = applicationService.updateApplicationStatus(app.getId(), companyPrincipal, update);

        assertNotNull(updated);
        assertEquals(ApplicationStatus.UNDER_REVIEW, updated.getStatus());
    }
}
