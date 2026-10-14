package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.InternshipRecord;
import com.example.internshipmanagement.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternshipRecordRepository extends JpaRepository<InternshipRecord, Long> {
    Page<InternshipRecord> findByStudentId(Long studentId, Pageable pageable);
    List<InternshipRecord> findByStudentId(Long studentId);
    Page<InternshipRecord> findByCompanyId(Long companyId, Pageable pageable);
    List<InternshipRecord> findByCompanyId(Long companyId);
    Optional<InternshipRecord> findByApplicationId(Long applicationId);
    long countByCompanyId(Long companyId);
    long countByStatus(RecordStatus status);
}
