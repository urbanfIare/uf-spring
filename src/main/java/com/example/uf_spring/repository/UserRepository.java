package com.example.uf_spring.repository;

import com.example.uf_spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);
    
    // 이름으로 사용자 찾기
    Optional<User> findByName(String name);
    
    // 나이로 사용자 찾기 (나이가 특정 값 이상인 사용자들)
    List<User> findByAgeGreaterThanEqual(int age);
} 