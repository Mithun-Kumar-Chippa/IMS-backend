package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.Internship;
import com.example.internshipmanagement.enums.InternshipStatus;
import com.example.internshipmanagement.enums.WorkMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {

    Page<Internship> findByCompanyId(Long companyId, Pageable pageable);

    List<Internship> findByCompanyId(Long companyId);

    Page<Internship> findByStatus(InternshipStatus status, Pageable pageable);

    @Query("SELECT i FROM Internship i WHERE i.status = :status " +
           "AND (:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(i.department) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(i.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:workMode IS NULL OR i.workMode = :workMode) " +
           "AND (:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:minStipend IS NULL OR i.stipend >= :minStipend) " +
           "AND (:activeOnly = false OR i.deadline IS NULL OR i.deadline >= :today)")
    Page<Internship> searchInternships(
            @Param("status") InternshipStatus status,
            @Param("keyword") String keyword,
            @Param("workMode") WorkMode workMode,
            @Param("location") String location,
            @Param("minStipend") Double minStipend,
            @Param("activeOnly") boolean activeOnly,
            @Param("today") LocalDate today,
            Pageable pageable);

    long countByStatus(InternshipStatus status);
}
