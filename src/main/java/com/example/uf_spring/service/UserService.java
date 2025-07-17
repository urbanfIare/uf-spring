package com.example.uf_spring.service;

import com.example.uf_spring.model.User;
import com.example.uf_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserService() {
        // 애플리케이션 시작 시 샘플 데이터 추가
    }
    
    // 애플리케이션 시작 후 샘플 데이터 초기화
    public void initializeSampleData() {
        if (userRepository.count() == 0) {
            userRepository.save(new User("김철수", "kim@example.com", 25));
            userRepository.save(new User("이영희", "lee@example.com", 30));
            userRepository.save(new User("박민수", "park@example.com", 28));
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