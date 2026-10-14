package com.example.internshipmanagement.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterStudentRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    private String phone;
    private String collegeName;
    private String degree;
    private String department;
    private Integer graduationYear;
    private Double gpa;
    private String skills;
    private String careerInterests;

    public RegisterStudentRequest() {}

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }

    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getCareerInterests() { return careerInterests; }
    public void setCareerInterests(String careerInterests) { this.careerInterests = careerInterests; }
}
