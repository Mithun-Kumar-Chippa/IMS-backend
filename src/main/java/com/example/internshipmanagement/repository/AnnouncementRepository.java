package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.Announcement;
import com.example.internshipmanagement.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByActiveTrueOrderByCreatedAtDesc();

    Page<Announcement> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM Announcement a WHERE a.active = true AND (a.targetRole IS NULL OR a.targetRole = :role) ORDER BY a.createdAt DESC")
    List<Announcement> findActiveByRole(@Param("role") Role role);
}
