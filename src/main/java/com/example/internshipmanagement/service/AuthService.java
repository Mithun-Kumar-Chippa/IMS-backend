package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.auth.*;
import com.example.internshipmanagement.entity.CompanyProfile;
import com.example.internshipmanagement.entity.StudentProfile;
import com.example.internshipmanagement.entity.User;
import com.example.internshipmanagement.enums.Role;
import com.example.internshipmanagement.exception.BadRequestException;
import com.example.internshipmanagement.exception.DuplicateResourceException;
import com.example.internshipmanagement.exception.ResourceNotFoundException;
import com.example.internshipmanagement.repository.CompanyProfileRepository;
import com.example.internshipmanagement.repository.StudentProfileRepository;
import com.example.internshipmanagement.repository.UserRepository;
import com.example.internshipmanagement.security.JwtTokenProvider;
import com.example.internshipmanagement.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogService auditLogService;

    public AuthService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       CompanyProfileRepository companyProfileRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthResponse registerStudent(RegisterStudentRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = new User(
                request.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getPhone(),
                Role.STUDENT
        );
        User savedUser = userRepository.save(user);

        StudentProfile profile = new StudentProfile(savedUser);
        profile.setCollegeName(request.getCollegeName());
        profile.setDegree(request.getDegree());
        profile.setDepartment(request.getDepartment());
        profile.setGraduationYear(request.getGraduationYear());
        profile.setGpa(request.getGpa());
        profile.setSkills(request.getSkills());
        profile.setCareerInterests(request.getCareerInterests());
        studentProfileRepository.save(profile);

        String token = tokenProvider.generateTokenFromUser(savedUser.getEmail(), savedUser.getId(), savedUser.getRole().name());

        auditLogService.logAction("STUDENT_REGISTER", savedUser.getFullName(), savedUser.getEmail(),
                "Registered student account", "N/A");

        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole(), savedUser.isActive());
    }

    @Transactional
    public AuthResponse registerCompany(RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = new User(
                request.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getPhone(),
                Role.COMPANY
        );
        User savedUser = userRepository.save(user);

        CompanyProfile profile = new CompanyProfile(savedUser, request.getCompanyName());
        profile.setIndustry(request.getIndustry());
        profile.setWebsite(request.getWebsite());
        profile.setLocation(request.getLocation());
        profile.setDescription(request.getDescription());
        profile.setVerified(false); // requires admin verification
        companyProfileRepository.save(profile);

        String token = tokenProvider.generateTokenFromUser(savedUser.getEmail(), savedUser.getId(), savedUser.getRole().name());

        auditLogService.logAction("COMPANY_REGISTER", savedUser.getFullName(), savedUser.getEmail(),
                "Registered company account: " + request.getCompanyName(), "N/A");

        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole(), savedUser.isActive(), false);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new BadRequestException("Your account is deactivated. Please contact the administrator.");
        }

        String token = tokenProvider.generateToken(authentication);

        boolean verified = true;
        if (user.getRole() == Role.COMPANY) {
            verified = companyProfileRepository.findByUserId(user.getId())
                    .map(CompanyProfile::isVerified)
                    .orElse(false);
        }

        auditLogService.logAction("USER_LOGIN", user.getFullName(), user.getEmail(),
                "User successfully logged in with role " + user.getRole(), "N/A");

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.isActive(), verified);
    }

    @Transactional
    public void changePassword(UserPrincipal principal, ChangePasswordRequest request) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.logAction("PASSWORD_CHANGE", user.getFullName(), user.getEmail(),
                "Password changed successfully", "N/A");
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + principal.getId()));

        return new UserSummaryDto(user.getId(), user.getEmail(), user.getFullName(), user.getPhone(),
                user.getRole(), user.isActive(), user.getCreatedAt());
    }
}
