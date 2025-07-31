package com.example.uf_spring.service;

import com.example.uf_spring.model.User;
import com.example.uf_spring.model.Role;
import com.example.uf_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 애플리케이션 시작 후 샘플 데이터 초기화
    public void initializeSampleData() {
        System.out.println("🔍 initializeSampleData() 시작");
        System.out.println("📊 현재 DB 사용자 수: " + userRepository.count());
        
        if (userRepository.count() == 0) {
            System.out.println("➕ 새 사용자 생성 중...");
            
            // BCrypt로 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode("admin");
            
            User newUser = new User("노경환", "admin", encodedPassword, 31, Role.ADMIN);
            User savedUser = userRepository.save(newUser);
            
            System.out.println("✅ admin 계정이 생성되었습니다!");
            System.out.println("👤 사용자명: admin");
            System.out.println("🔑 비밀번호: admin");
            System.out.println("🆔 생성된 사용자 ID: " + savedUser.getId());
            System.out.println("🔐 암호화된 비밀번호: " + encodedPassword);
        } else {
            System.out.println("⚠️ 이미 사용자가 존재합니다. 새 사용자를 생성하지 않습니다.");
        }
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public Optional<User> updateUser(Long id, User userDetails) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setAge(userDetails.getAge());
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }
    
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> getUsersByAgeGreaterThan(int age) {
        return userRepository.findByAgeGreaterThanEqual(age);
    }
} 