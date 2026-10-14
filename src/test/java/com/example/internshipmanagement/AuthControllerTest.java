package com.example.internshipmanagement;

import com.example.internshipmanagement.dto.auth.LoginRequest;
import com.example.internshipmanagement.dto.auth.RegisterStudentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully authenticate existing seeded student")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("student@ims.edu", "Student@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.email", is("student@ims.edu")))
                .andExpect(jsonPath("$.data.role", is("STUDENT")));
    }

    @Test
    @DisplayName("Should reject login with invalid password")
    void testLoginFailureBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("student@ims.edu", "WrongPassword999");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Unauthorized")));
    }

    @Test
    @DisplayName("Should successfully register a new student")
    void testRegisterNewStudent() throws Exception {
        RegisterStudentRequest request = new RegisterStudentRequest();
        request.setEmail("newstudent" + System.currentTimeMillis() + "@test.edu");
        request.setPassword("Secret@123");
        request.setFullName("Jane Doe");
        request.setCollegeName("Apex Tech University");
        request.setDegree("B.E.");
        request.setDepartment("Computer Engineering");
        request.setGraduationYear(2027);

        mockMvc.perform(post("/api/v1/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.fullName", is("Jane Doe")));
    }

    @Test
    @DisplayName("Should fail registration on duplicate email")
    void testDuplicateEmailRegistration() throws Exception {
        RegisterStudentRequest request = new RegisterStudentRequest();
        request.setEmail("student@ims.edu"); // already seeded
        request.setPassword("Secret@123");
        request.setFullName("Duplicate User");

        mockMvc.perform(post("/api/v1/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Conflict")));
    }
}
