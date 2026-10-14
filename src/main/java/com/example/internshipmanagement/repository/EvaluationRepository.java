package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findByInternshipRecordId(Long recordId);
}
