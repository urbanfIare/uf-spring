package com.example.uf_spring.service;

import com.example.uf_spring.dto.PostDto;
import com.example.uf_spring.model.Post;
import com.example.uf_spring.model.PostCategory;
import com.example.uf_spring.model.User;
import com.example.uf_spring.repository.PostRepository;
import com.example.uf_spring.repository.CommentRepository;
import com.example.uf_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 게시글 생성
    public PostDto.Response createPost(PostDto.CreateRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                author,
                request.getCategory()
        );
        
        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost);
    }

    // 모든 게시글 조회
    public List<PostDto.Response> getAllPosts() {
        List<Post> posts = postRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
        return posts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회 (조회수 증가)
    public PostDto.Response getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        if (post.isDeleted()) {
            throw new RuntimeException("삭제된 게시글입니다.");
        }
        
        // 조회수 증가
        post.incrementViewCount();
        postRepository.save(post);
        
        return convertToResponse(post);
    }

    // 게시글 수정
    public PostDto.Response updatePost(Long id, PostDto.UpdateRequest request, Long authorId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        if (post.isDeleted()) {
            throw new RuntimeException("삭제된 게시글입니다.");
        }
        
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }
        
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.updateModifiedTime();
        
        Post updatedPost = postRepository.save(post);
        return convertToResponse(updatedPost);
    }

    // 게시글 삭제 (소프트 삭제)
    public boolean deletePost(Long id, Long authorId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        if (!post.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }
        
        post.setDeleted(true);
        postRepository.save(post);
        return true;
    }

    // 카테고리별 게시글 조회
    public List<PostDto.Response> getPostsByCategory(PostCategory category) {
        List<Post> posts = postRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(category);
        return posts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 검색 기능
    public List<PostDto.Response> searchPosts(String keyword) {
        List<Post> posts = postRepository.findByTitleOrContentContaining(keyword);
        return posts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 인기 게시글 조회 (조회수 기준)
    public List<PostDto.Response> getPopularPosts() {
        List<Post> posts = postRepository.findByIsDeletedFalseOrderByViewCountDesc();
        return posts.stream()
                .limit(10) // 상위 10개만
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 좋아요 많은 게시글 조회
    public List<PostDto.Response> getMostLikedPosts() {
        List<Post> posts = postRepository.findByIsDeletedFalseOrderByLikeCountDesc();
        return posts.stream()
                .limit(10) // 상위 10개만
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 최근 게시글 조회 (최근 7일)
    public List<PostDto.Response> getRecentPosts() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<Post> posts = postRepository.findRecentPosts(weekAgo);
        return posts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 좋아요 증가
    public PostDto.Response likePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        post.incrementLikeCount();
        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost);
    }

    // 좋아요 감소
    public PostDto.Response unlikePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        post.decrementLikeCount();
        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost);
    }

    // Entity를 DTO로 변환
    private PostDto.Response convertToResponse(Post post) {
        PostDto.Response response = new PostDto.Response();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setAuthorName(post.getAuthor().getName());
        response.setAuthorId(post.getAuthor().getId());
        response.setCategory(post.getCategory());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(post.getLikeCount());
        
        // 댓글 수 계산
        long commentCount = commentRepository.countByPostIdAndIsDeletedFalse(post.getId());
        response.setCommentCount((int) commentCount);
        
        return response;
    }
    
    // 고급 검색 및 정렬 기능
    public PostDto.PageResponse searchPostsWithPaging(PostDto.SearchRequest request) {
        // 정렬 설정
        Sort sort = createSort(request.getSortBy(), request.getSortOrder());
        
        // 페이징 설정
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // 검색 조건에 따른 쿼리 실행
        Page<Post> postPage;
        
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            if (request.getCategory() != null) {
                // 키워드 + 카테고리 검색
                postPage = postRepository.findByTitleContainingOrContentContainingAndCategoryAndIsDeletedFalse(
                        request.getKeyword(), request.getCategory(), pageable);
            } else {
                // 키워드만 검색
                postPage = postRepository.findByTitleContainingOrContentContainingAndIsDeletedFalse(
                        request.getKeyword(), pageable);
            }
        } else if (request.getCategory() != null) {
            // 카테고리만 검색
            postPage = postRepository.findByCategoryAndIsDeletedFalse(request.getCategory(), pageable);
        } else {
            // 전체 조회
            postPage = postRepository.findByIsDeletedFalse(pageable);
        }
        
        // DTO 변환
        List<PostDto.Response> responses = postPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PostDto.PageResponse(
                responses,
                request.getPage(),
                request.getSize(),
                postPage.getTotalElements(),
                postPage.getTotalPages()
        );
    }
    
    // 정렬 설정 생성
    private Sort createSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        switch (sortBy != null ? sortBy.toLowerCase() : "latest") {
            case "popular":
                return Sort.by(direction, "viewCount");
            case "mostliked":
                return Sort.by(direction, "likeCount");
            case "mostviewed":
                return Sort.by(direction, "viewCount");
            case "latest":
            default:
                return Sort.by(direction, "createdAt");
        }
    }
    
    // 조회수 증가 (별도 메서드)
    public void incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.incrementViewCount();
        postRepository.save(post);
    }
} 