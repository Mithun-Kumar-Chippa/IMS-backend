package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.StudentProfile;
import com.example.internshipmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUser(User user);
    Optional<StudentProfile> findByUserId(Long userId);
}
