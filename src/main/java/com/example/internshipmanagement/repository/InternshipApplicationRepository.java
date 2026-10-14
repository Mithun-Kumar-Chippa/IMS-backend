package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.InternshipApplication;
import com.example.internshipmanagement.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternshipApplicationRepository extends JpaRepository<InternshipApplication, Long> {

    Page<InternshipApplication> findByStudentId(Long studentId, Pageable pageable);

    List<InternshipApplication> findByStudentId(Long studentId);

    Page<InternshipApplication> findByInternshipId(Long internshipId, Pageable pageable);

    List<InternshipApplication> findByInternshipId(Long internshipId);

    @Query("SELECT a FROM InternshipApplication a WHERE a.internship.company.id = :companyId")
    Page<InternshipApplication> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT a FROM InternshipApplication a WHERE a.internship.company.id = :companyId AND (:status IS NULL OR a.status = :status)")
    Page<InternshipApplication> findByCompanyIdAndStatus(
            @Param("companyId") Long companyId,
            @Param("status") ApplicationStatus status,
            Pageable pageable);

    boolean existsByInternshipIdAndStudentId(Long internshipId, Long studentId);

    Optional<InternshipApplication> findByInternshipIdAndStudentId(Long internshipId, Long studentId);

    long countByStatus(ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM InternshipApplication a WHERE a.internship.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(a) FROM InternshipApplication a WHERE a.internship.company.id = :companyId AND a.status = :status")
    long countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") ApplicationStatus status);
}
