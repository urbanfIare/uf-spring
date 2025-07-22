package com.example.uf_spring.service;

import com.example.uf_spring.dto.ReportDto;
import com.example.uf_spring.model.Report;
import com.example.uf_spring.model.Report.ReportStatus;
import com.example.uf_spring.model.User;
import com.example.uf_spring.model.Post;
import com.example.uf_spring.model.Comment;
import com.example.uf_spring.repository.ReportRepository;
import com.example.uf_spring.repository.UserRepository;
import com.example.uf_spring.repository.PostRepository;
import com.example.uf_spring.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    // 신고 등록
    public ReportDto.Response createReport(ReportDto.CreateRequest req) {
        User reporter = userRepository.findById(req.getReporterId())
                .orElseThrow(() -> new RuntimeException("신고자를 찾을 수 없습니다."));
        Post post = req.getPostId() != null ? postRepository.findById(req.getPostId()).orElse(null) : null;
        Comment comment = req.getCommentId() != null ? commentRepository.findById(req.getCommentId()).orElse(null) : null;
        if (post == null && comment == null) throw new RuntimeException("신고 대상이 없습니다.");
        Report report = new Report();
        report.setReporter(reporter);
        report.setPost(post);
        report.setComment(comment);
        report.setReason(req.getReason());
        report.setStatus(ReportStatus.PENDING);
        Report saved = reportRepository.save(report);
        return toDto(saved);
    }

    // 전체/상태별 신고 목록
    public List<ReportDto.Response> getReports(ReportStatus status) {
        List<Report> list = (status == null) ? reportRepository.findAll() : reportRepository.findByStatus(status);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    // 신고 처리(상태 변경)
    public ReportDto.Response updateStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고 내역을 찾을 수 없습니다."));
        report.setStatus(status);
        Report saved = reportRepository.save(report);
        return toDto(saved);
    }

    private ReportDto.Response toDto(Report report) {
        return new ReportDto.Response(
                report.getId(),
                report.getReporter().getName(),
                report.getPost() != null ? report.getPost().getId() : null,
                report.getComment() != null ? report.getComment().getId() : null,
                report.getReason(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }
} 