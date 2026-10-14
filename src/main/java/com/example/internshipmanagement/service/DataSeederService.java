package com.example.internshipmanagement.service;

import com.example.internshipmanagement.entity.*;
import com.example.internshipmanagement.enums.*;
import com.example.internshipmanagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DataSeederService implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeederService.class);

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final InternshipRecordRepository recordRepository;
    private final ProgressReportRepository progressReportRepository;
    private final EvaluationRepository evaluationRepository;
    private final AnnouncementRepository announcementRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@ims.edu}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@12345}")
    private String adminPassword;

    @Value("${app.admin.name:System Administrator}")
    private String adminName;

    public DataSeederService(UserRepository userRepository,
                             StudentProfileRepository studentProfileRepository,
                             CompanyProfileRepository companyProfileRepository,
                             InternshipRepository internshipRepository,
                             InternshipApplicationRepository applicationRepository,
                             InternshipRecordRepository recordRepository,
                             ProgressReportRepository progressReportRepository,
                             EvaluationRepository evaluationRepository,
                             AnnouncementRepository announcementRepository,
                             NotificationRepository notificationRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
        this.recordRepository = recordRepository;
        this.progressReportRepository = progressReportRepository;
        this.evaluationRepository = evaluationRepository;
        this.announcementRepository = announcementRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            logger.info("Database already seeded with {} users. Skipping initial seed.", userRepository.count());
            return;
        }

        logger.info("Seeding initial database records for Internship Management System...");

        // 1. Seed Administrator
        User admin = new User(adminEmail, passwordEncoder.encode(adminPassword), adminName, "+91 9876543210", Role.ADMIN);
        admin = userRepository.save(admin);

        // 2. Seed Verified Companies
        User techCorpUser = new User("techcorp@example.com", passwordEncoder.encode("Company@123"), "TechNova Solutions HR", "+91 9876500001", Role.COMPANY);
        techCorpUser = userRepository.save(techCorpUser);
        CompanyProfile techCorpProfile = new CompanyProfile(techCorpUser, "TechNova Solutions");
        techCorpProfile.setIndustry("Enterprise Software & Cloud");
        techCorpProfile.setWebsite("https://technova.example.com");
        techCorpProfile.setLocation("Bengaluru, India");
        techCorpProfile.setDescription("Leading enterprise cloud infrastructure and modern web application development firm.");
        techCorpProfile.setVerified(true);
        techCorpProfile.setVerificationNotes("Verified enterprise partner.");
        companyProfileRepository.save(techCorpProfile);

        User cloudScaleUser = new User("cloudscale@example.com", passwordEncoder.encode("Company@123"), "CloudScale Talent Team", "+91 9876500002", Role.COMPANY);
        cloudScaleUser = userRepository.save(cloudScaleUser);
        CompanyProfile cloudScaleProfile = new CompanyProfile(cloudScaleUser, "CloudScale Networks");
        cloudScaleProfile.setIndustry("DevOps & Cybersecurity");
        cloudScaleProfile.setWebsite("https://cloudscale.example.com");
        cloudScaleProfile.setLocation("Hyderabad, India");
        cloudScaleProfile.setDescription("Pioneering site reliability engineering, Kubernetes consulting, and cyber defense solutions.");
        cloudScaleProfile.setVerified(true);
        cloudScaleProfile.setVerificationNotes("Verified campus recruiter.");
        companyProfileRepository.save(cloudScaleProfile);

        User pendingCompanyUser = new User("innovate@example.com", passwordEncoder.encode("Company@123"), "Innovate AI Founder", "+91 9876500003", Role.COMPANY);
        pendingCompanyUser = userRepository.save(pendingCompanyUser);
        CompanyProfile pendingCompanyProfile = new CompanyProfile(pendingCompanyUser, "Innovate AI Labs");
        pendingCompanyProfile.setIndustry("Artificial Intelligence");
        pendingCompanyProfile.setWebsite("https://innovateai.example.com");
        pendingCompanyProfile.setLocation("Pune, India");
        pendingCompanyProfile.setDescription("Early stage generative AI startup building domain agents.");
        pendingCompanyProfile.setVerified(false); // pending admin verification
        companyProfileRepository.save(pendingCompanyProfile);

        // 3. Seed Students
        User alex = new User("student@ims.edu", passwordEncoder.encode("Student@123"), "Alex Chen", "+91 9123456780", Role.STUDENT);
        alex = userRepository.save(alex);
        StudentProfile alexProfile = new StudentProfile(alex);
        alexProfile.setCollegeName("National Institute of Technology");
        alexProfile.setDegree("B.Tech");
        alexProfile.setDepartment("Computer Science & Engineering");
        alexProfile.setGraduationYear(2026);
        alexProfile.setGpa(3.85);
        alexProfile.setSkills("Java, Spring Boot, React, MySQL, REST APIs, version control");
        alexProfile.setCareerInterests("Full Stack Development, Backend Engineering");
        alexProfile.setResumeUrl("/uploads/alex_chen_resume.pdf");
        alexProfile.setResumeFileName("alex_chen_resume.pdf");
        studentProfileRepository.save(alexProfile);

        User priya = new User("priya@ims.edu", passwordEncoder.encode("Student@123"), "Priya Sharma", "+91 9123456781", Role.STUDENT);
        priya = userRepository.save(priya);
        StudentProfile priyaProfile = new StudentProfile(priya);
        priyaProfile.setCollegeName("National Institute of Technology");
        priyaProfile.setDegree("B.Tech");
        priyaProfile.setDepartment("Information Technology");
        priyaProfile.setGraduationYear(2026);
        priyaProfile.setGpa(3.92);
        priyaProfile.setSkills("React, TypeScript, CSS, Bootstrap, UI/UX Design, Figma");
        priyaProfile.setCareerInterests("Frontend Engineering, Product Design");
        studentProfileRepository.save(priyaProfile);

        User david = new User("david@ims.edu", passwordEncoder.encode("Student@123"), "David Miller", "+91 9123456782", Role.STUDENT);
        david = userRepository.save(david);
        StudentProfile davidProfile = new StudentProfile(david);
        davidProfile.setCollegeName("State Engineering College");
        davidProfile.setDegree("B.Tech");
        davidProfile.setDepartment("Software Engineering");
        davidProfile.setGraduationYear(2027);
        davidProfile.setGpa(3.70);
        davidProfile.setSkills("Python, Docker, Linux, CI/CD, AWS, Kubernetes");
        davidProfile.setCareerInterests("DevOps, Cloud Engineering");
        studentProfileRepository.save(davidProfile);

        // 4. Seed Internships
        Internship intern1 = new Internship();
        intern1.setCompany(techCorpUser);
        intern1.setTitle("Full Stack Java Developer Intern");
        intern1.setDescription("Build and scale production microservices using Spring Boot 3, Java 21, and React 18. Work alongside experienced backend architects on high-throughput REST APIs.");
        intern1.setDepartment("Software Engineering");
        intern1.setLocation("Bengaluru, India");
        intern1.setWorkMode(WorkMode.HYBRID);
        intern1.setStipend(25000.0);
        intern1.setDurationWeeks(12);
        intern1.setRequirements("Proficiency in Core Java, Spring Boot, JPA/Hibernate, relational databases, and modern JavaScript/React.");
        intern1.setResponsibilities("Develop RESTful endpoints, implement JWT authentication filters, write JUnit test cases, and create responsive UI views.");
        intern1.setDeadline(LocalDate.now().plusDays(30));
        intern1.setStatus(InternshipStatus.APPROVED);
        intern1.setMaxPositions(3);
        intern1 = internshipRepository.save(intern1);

        Internship intern2 = new Internship();
        intern2.setCompany(techCorpUser);
        intern2.setTitle("Frontend UI/UX React Intern");
        intern2.setDescription("Design modern web interfaces using React, Bootstrap 5, and state management frameworks. Optimize web application performance and accessibility.");
        intern2.setDepartment("Product Design");
        intern2.setLocation("Remote");
        intern2.setWorkMode(WorkMode.REMOTE);
        intern2.setStipend(20000.0);
        intern2.setDurationWeeks(8);
        intern2.setRequirements("Strong knowledge of HTML5, CSS3, JavaScript, React hooks, Axios, and component architecture.");
        intern2.setResponsibilities("Implement pixel-perfect responsive dashboard screens, integrate backend REST APIs, and write client test cases.");
        intern2.setDeadline(LocalDate.now().plusDays(45));
        intern2.setStatus(InternshipStatus.APPROVED);
        intern2.setMaxPositions(2);
        intern2 = internshipRepository.save(intern2);

        Internship intern3 = new Internship();
        intern3.setCompany(cloudScaleUser);
        intern3.setTitle("Cloud & DevOps Engineering Intern");
        intern3.setDescription("Work on automated CI/CD pipelines, container orchestration with Docker, and cloud infrastructure monitoring.");
        intern3.setDepartment("Infrastructure & Reliability");
        intern3.setLocation("Hyderabad, India");
        intern3.setWorkMode(WorkMode.ON_SITE);
        intern3.setStipend(30000.0);
        intern3.setDurationWeeks(16);
        intern3.setRequirements("Experience with Linux commands, Docker containers, basic networking, and scripting.");
        intern3.setResponsibilities("Configure Docker Compose multi-service stacks, automate builds, and configure Actuator health monitoring.");
        intern3.setDeadline(LocalDate.now().plusDays(20));
        intern3.setStatus(InternshipStatus.APPROVED);
        intern3.setMaxPositions(2);
        intern3 = internshipRepository.save(intern3);

        Internship internPending = new Internship();
        internPending.setCompany(pendingCompanyUser);
        internPending.setTitle("AI Research & Model Integration Intern");
        internPending.setDescription("Develop retrieval-augmented generation pipelines, evaluate embedding similarity, and prototype agent workflows.");
        internPending.setDepartment("AI Research");
        internPending.setLocation("Remote");
        internPending.setWorkMode(WorkMode.REMOTE);
        internPending.setStipend(28000.0);
        internPending.setDurationWeeks(12);
        internPending.setRequirements("Familiarity with Python, vector databases, prompt engineering, and LLM APIs.");
        internPending.setResponsibilities("Benchmark cosine similarity retrieval, curate test corpora, and build agent integration prototypes.");
        internPending.setDeadline(LocalDate.now().plusDays(60));
        internPending.setStatus(InternshipStatus.PENDING_APPROVAL); // for admin to review
        internPending.setMaxPositions(1);
        internshipRepository.save(internPending);

        // 5. Seed Applications in Different Lifecycle States
        // App 1: Alex Chen -> Full Stack Java Developer (ACCEPTED)
        InternshipApplication app1 = new InternshipApplication();
        app1.setInternship(intern1);
        app1.setStudent(alex);
        app1.setCoverLetter("I have hands-on experience building full-stack web applications with Spring Boot and React. I am excited to contribute to TechNova's scalable microservices.");
        app1.setResumeUrl(alexProfile.getResumeUrl());
        app1.setStatus(ApplicationStatus.ACCEPTED);
        app1.setReviewedAt(LocalDateTime.now().minusDays(5));
        app1.setMentorName("Suresh Rao (Principal Architect)");
        app1.setReviewerNotes("Outstanding interview performance and strong core Java foundations. Selected for 12-week internship.");
        app1 = applicationRepository.save(app1);

        // Create InternshipRecord for Alex
        InternshipRecord record1 = new InternshipRecord();
        record1.setApplication(app1);
        record1.setStudent(alex);
        record1.setCompany(techCorpUser);
        record1.setInternship(intern1);
        record1.setMentorName("Suresh Rao");
        record1.setMentorEmail("suresh.rao@technova.example.com");
        record1.setStartDate(LocalDate.now().minusWeeks(3));
        record1.setEndDate(LocalDate.now().plusWeeks(9));
        record1.setStatus(RecordStatus.IN_PROGRESS);
        record1.setFinalRemarks("Intern is demonstrating rapid progress.");
        record1 = recordRepository.save(record1);

        // Progress reports for record1
        ProgressReport report1 = new ProgressReport();
        report1.setInternshipRecord(record1);
        report1.setWeekNumber(1);
        report1.setReportTitle("Development Environment Setup and Architecture Onboarding");
        report1.setTasksCompleted("Set up Maven, Docker, MySQL 8, and Spring Boot 3 workspace. Studied existing REST API architecture and entity relationships.");
        report1.setChallengesFaced("Configuring CORS credentials across Vite dev server and Spring Security filter chain.");
        report1.setPlansForNextWeek("Implement JWT authentication filters and test protected endpoints.");
        report1.setHoursWorked(40);
        report1.setFeedback("Great start! You solved the CORS configuration swiftly.");
        report1.setReviewedAt(LocalDateTime.now().minusWeeks(2));
        progressReportRepository.save(report1);

        ProgressReport report2 = new ProgressReport();
        report2.setInternshipRecord(record1);
        report2.setWeekNumber(2);
        report2.setReportTitle("Authentication Service & BCrypt Integration");
        report2.setTasksCompleted("Implemented AuthService with BCrypt password hashing, wrote unit tests for duplicate email validation, and integrated token issuance.");
        report2.setChallengesFaced("Handling expired token edge cases in JwtAuthenticationFilter.");
        report2.setPlansForNextWeek("Connect React login context and Axios authorization interceptor.");
        report2.setHoursWorked(42);
        report2.setFeedback("Excellent code quality and adherence to clean architecture principles.");
        report2.setReviewedAt(LocalDateTime.now().minusWeeks(1));
        progressReportRepository.save(report2);

        // App 2: Priya Sharma -> Frontend React Intern (SHORTLISTED)
        InternshipApplication app2 = new InternshipApplication();
        app2.setInternship(intern2);
        app2.setStudent(priya);
        app2.setCoverLetter("I specialize in responsive UI engineering with Bootstrap 5 and React. I would love to build accessible dashboards for TechNova.");
        app2.setResumeUrl(alexProfile.getResumeUrl());
        app2.setStatus(ApplicationStatus.SHORTLISTED);
        app2.setReviewedAt(LocalDateTime.now().minusDays(2));
        app2.setReviewerNotes("Impressive portfolio and design sensitivity. Scheduled for technical interview round.");
        applicationRepository.save(app2);

        // App 3: David Miller -> Cloud & DevOps (UNDER_REVIEW)
        InternshipApplication app3 = new InternshipApplication();
        app3.setInternship(intern3);
        app3.setStudent(david);
        app3.setCoverLetter("I have built multi-container Docker applications and managed CI/CD pipelines. Looking forward to learning enterprise SRE practices.");
        app3.setResumeUrl(alexProfile.getResumeUrl());
        app3.setStatus(ApplicationStatus.UNDER_REVIEW);
        app3.setReviewedAt(LocalDateTime.now().minusDays(1));
        app3.setReviewerNotes("Reviewing academic transcripts and certifications.");
        applicationRepository.save(app3);

        // App 4: Priya Sharma -> Full Stack Java Developer (APPLIED)
        InternshipApplication app4 = new InternshipApplication();
        app4.setInternship(intern1);
        app4.setStudent(priya);
        app4.setCoverLetter("Seeking full stack exposure to expand backend skills with Java and Spring Data JPA.");
        app4.setResumeUrl(alexProfile.getResumeUrl());
        app4.setStatus(ApplicationStatus.APPLIED);
        applicationRepository.save(app4);

        // 6. Seed System Announcements
        Announcement ann1 = new Announcement(
                "Welcome to the Centralized Internship Portal 2026",
                "Students and verified companies can now register, post internship listings, and track applications seamlessly.",
                null,
                admin
        );
        announcementRepository.save(ann1);

        Announcement ann2 = new Announcement(
                "Resume & Viva Voce Guidance Session",
                "All 3rd and 4th year students are advised to keep their PDF resume updated and review weekly syllabus mapping topics for upcoming practical assessments.",
                Role.STUDENT,
                admin
        );
        announcementRepository.save(ann2);

        Announcement ann3 = new Announcement(
                "Campus Recruiter Verification Guidelines",
                "Partner companies are requested to complete company profile verification to enable instant publishing of internship positions.",
                Role.COMPANY,
                admin
        );
        announcementRepository.save(ann3);

        // 7. Seed Notifications
        Notification notif1 = new Notification(
                alex,
                "Internship Application Accepted!",
                "Congratulations! TechNova Solutions accepted your application for 'Full Stack Java Developer Intern'. Mentor: Suresh Rao.",
                NotificationType.APPLICATION_STATUS,
                app1.getId()
        );
        notificationRepository.save(notif1);

        Notification notif2 = new Notification(
                priya,
                "Application Shortlisted",
                "Your application for 'Frontend UI/UX React Intern' has been shortlisted by TechNova Solutions.",
                NotificationType.APPLICATION_STATUS,
                app2.getId()
        );
        notificationRepository.save(notif2);

        Notification notif3 = new Notification(
                techCorpUser,
                "New Student Applications",
                "You have received 2 new student applications for your posted internship positions.",
                NotificationType.NEW_APPLICATION,
                intern1.getId()
        );
        notificationRepository.save(notif3);

        logger.info("Database seeding completed successfully! Seeded Admin ({}), 3 Companies, 3 Students, 4 Internships, and applications.", adminEmail);
    }
}
