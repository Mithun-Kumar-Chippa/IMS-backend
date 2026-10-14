package com.example.internshipmanagement.dto.profile;

public class StudentProfileDto {
    private Long id;
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String collegeName;
    private String degree;
    private String department;
    private Integer graduationYear;
    private Double gpa;
    private String skills;
    private String careerInterests;
    private String resumeUrl;
    private String resumeFileName;

    public StudentProfileDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }
}
