package com.example.uf_spring.repository;

import com.example.uf_spring.model.Bookmark;
import com.example.uf_spring.model.User;
import com.example.uf_spring.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserAndPost(User user, Post post);
    List<Bookmark> findByUser(User user);
    void deleteByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
} 