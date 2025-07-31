package com.example.uf_spring.service;

import com.example.uf_spring.dto.CommentDto;
import com.example.uf_spring.model.Comment;
import com.example.uf_spring.model.Post;
import com.example.uf_spring.model.User;
import com.example.uf_spring.repository.CommentRepository;
import com.example.uf_spring.repository.PostRepository;
import com.example.uf_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 생성
    public CommentDto.Response createComment(Long postId, CommentDto.CreateRequest request, Long authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        if (post.isDeleted()) {
            throw new RuntimeException("삭제된 게시글에는 댓글을 작성할 수 없습니다.");
        }
        
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Comment comment = new Comment(
                request.getContent(),
                author,
                post
        );
        
        Comment savedComment = commentRepository.save(comment);
        return convertToResponse(savedComment);
    }

    // 게시글의 모든 댓글 조회
    public List<CommentDto.Response> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId);
        return comments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 댓글 수정
    public CommentDto.Response updateComment(Long commentId, CommentDto.UpdateRequest request, Long authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        if (comment.isDeleted()) {
            throw new RuntimeException("삭제된 댓글입니다.");
        }
        
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("댓글을 수정할 권한이 없습니다.");
        }
        
        comment.setContent(request.getContent());
        comment.updateModifiedTime();
        
        Comment updatedComment = commentRepository.save(comment);
        return convertToResponse(updatedComment);
    }

    // 댓글 삭제 (소프트 삭제)
    public boolean deleteComment(Long commentId, Long authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }
        
        comment.setDeleted(true);
        commentRepository.save(comment);
        return true;
    }

    // 사용자의 모든 댓글 조회
    public List<CommentDto.Response> getCommentsByAuthorId(Long authorId) {
        List<Comment> comments = commentRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(authorId);
        return comments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Entity를 DTO로 변환
    private CommentDto.Response convertToResponse(Comment comment) {
        CommentDto.Response response = new CommentDto.Response();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setAuthorName(comment.getAuthor().getName());
        response.setAuthorId(comment.getAuthor().getId());
        response.setPostId(comment.getPost().getId());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
} 