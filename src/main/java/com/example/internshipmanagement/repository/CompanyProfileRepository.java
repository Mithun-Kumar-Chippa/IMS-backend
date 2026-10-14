package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.CompanyProfile;
import com.example.internshipmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    Optional<CompanyProfile> findByUser(User user);
    Optional<CompanyProfile> findByUserId(Long userId);
    List<CompanyProfile> findByVerifiedFalse();
    Page<CompanyProfile> findByVerifiedFalse(Pageable pageable);
    long countByVerifiedTrue();
    long countByVerifiedFalse();
}
