package com.example.uf_spring.repository;

import com.example.uf_spring.model.Report;
import com.example.uf_spring.model.Report.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatus(ReportStatus status);
    List<Report> findByPostId(Long postId);
    List<Report> findByCommentId(Long commentId);
} 