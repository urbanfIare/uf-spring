package com.example.uf_spring.service;

import com.example.uf_spring.dto.BookmarkDto;
import com.example.uf_spring.model.Bookmark;
import com.example.uf_spring.model.User;
import com.example.uf_spring.model.Post;
import com.example.uf_spring.repository.BookmarkRepository;
import com.example.uf_spring.repository.UserRepository;
import com.example.uf_spring.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 북마크 추가 (CreateRequest 사용)
    public BookmarkDto.Response addBookmark(BookmarkDto.CreateRequest request) {
        return addBookmark(request.getUserId(), request.getPostId());
    }

    // 북마크 추가
    public BookmarkDto.Response addBookmark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        if (bookmarkRepository.existsByUserAndPost(user, post)) {
            throw new RuntimeException("이미 북마크한 게시글입니다.");
        }
        Bookmark bookmark = new Bookmark(user, post);
        Bookmark saved = bookmarkRepository.save(bookmark);
        return toDto(saved);
    }

    // 북마크 삭제 (ID로)
    public boolean deleteBookmark(Long bookmarkId) {
        Optional<Bookmark> bookmark = bookmarkRepository.findById(bookmarkId);
        if (bookmark.isPresent()) {
            bookmarkRepository.delete(bookmark.get());
            return true;
        }
        return false;
    }

    // 북마크 삭제
    public void removeBookmark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        bookmarkRepository.deleteByUserAndPost(user, post);
    }

    // 내 북마크 목록 조회
    public List<BookmarkDto.Response> getBookmarksByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<Bookmark> bookmarks = bookmarkRepository.findByUser(user);
        return bookmarks.stream().map(this::toDto).collect(Collectors.toList());
    }

    // 사용자별 북마크 조회 (별칭)
    public List<BookmarkDto.Response> getBookmarksByUserId(Long userId) {
        return getBookmarksByUser(userId);
    }

    // 북마크 여부 확인
    public boolean isBookmarked(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return bookmarkRepository.existsByUserAndPost(user, post);
    }

    private BookmarkDto.Response toDto(Bookmark bookmark) {
        return new BookmarkDto.Response(
                bookmark.getId(),
                bookmark.getUser().getId(),
                bookmark.getPost().getId(),
                bookmark.getPost().getTitle(),
                bookmark.getCreatedAt()
        );
    }
} 