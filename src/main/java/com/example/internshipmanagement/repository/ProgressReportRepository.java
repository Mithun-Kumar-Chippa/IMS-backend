package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.ProgressReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressReportRepository extends JpaRepository<ProgressReport, Long> {
    List<ProgressReport> findByInternshipRecordIdOrderByWeekNumberAsc(Long recordId);
}
