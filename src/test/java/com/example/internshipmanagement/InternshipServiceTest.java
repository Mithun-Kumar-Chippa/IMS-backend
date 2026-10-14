package com.example.internshipmanagement;

import com.example.internshipmanagement.dto.internship.InternshipFilterDto;
import com.example.internshipmanagement.dto.internship.InternshipRequest;
import com.example.internshipmanagement.dto.internship.InternshipResponse;
import com.example.internshipmanagement.enums.WorkMode;
import com.example.internshipmanagement.security.UserPrincipal;
import com.example.internshipmanagement.service.InternshipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InternshipServiceTest {

    @Autowired
    private InternshipService internshipService;

    @Test
    @DisplayName("Should successfully search seeded approved internships")
    void testSearchInternships() {
        InternshipFilterDto filter = new InternshipFilterDto();
        filter.setKeyword("Java");

        var response = internshipService.searchInternships(filter, PageRequest.of(0, 10, Sort.by("createdAt").descending()));

        assertNotNull(response);
        assertFalse(response.getContent().isEmpty());
        assertTrue(response.getContent().stream().anyMatch(i -> i.getTitle().contains("Java")));
    }

    @Test
    @DisplayName("Should create new internship opportunity for company")
    void testCreateInternship() {
        UserPrincipal companyPrincipal = new UserPrincipal(
                2L,
                "techcorp@example.com",
                "password",
                "TechNova Solutions HR",
                com.example.internshipmanagement.enums.Role.COMPANY,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_COMPANY"))
        );

        InternshipRequest request = new InternshipRequest();
        request.setTitle("Junior Mobile Developer Intern");
        request.setDescription("Build mobile interfaces using React Native.");
        request.setDepartment("Mobile Engineering");
        request.setLocation("Bengaluru");
        request.setWorkMode(WorkMode.HYBRID);
        request.setStipend(22000.0);
        request.setDurationWeeks(10);
        request.setDeadline(LocalDate.now().plusDays(40));

        InternshipResponse created = internshipService.createInternship(companyPrincipal, request);

        assertNotNull(created);
        assertEquals("Junior Mobile Developer Intern", created.getTitle());
        assertEquals(22000.0, created.getStipend());
    }
}
