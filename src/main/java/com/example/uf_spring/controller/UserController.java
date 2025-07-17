package com.example.uf_spring.controller;

import com.example.uf_spring.model.User;
import com.example.uf_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 모든 사용자 조회
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // ID로 사용자 조회 (PathVariable 예시)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 새 사용자 생성 (RequestBody 예시)
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // 사용자 정보 수정 (PathVariable + RequestBody 조합)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> updatedUser = userService.updateUser(id, userDetails);
        return updatedUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 사용자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 이메일로 사용자 조회 (PathVariable)
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 나이로 사용자 조회 (PathVariable)
    @GetMapping("/age/{age}")
    public ResponseEntity<List<User>> getUsersByAgeGreaterThan(@PathVariable int age) {
        List<User> users = userService.getUsersByAgeGreaterThan(age);
        return ResponseEntity.ok(users);
    }

    // ===== 새로운 예시들 =====
    
    // QueryParam 예시 - 검색 기능
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int minAge,
            @RequestParam(defaultValue = "100") int maxAge) {
        
        List<User> allUsers = userService.getAllUsers();
        
        // 필터링 로직 (간단한 예시)
        List<User> filteredUsers = allUsers.stream()
                .filter(user -> name == null || user.getName().contains(name))
                .filter(user -> email == null || user.getEmail().contains(email))
                .filter(user -> user.getAge() >= minAge && user.getAge() <= maxAge)
                .toList();
        
        return ResponseEntity.ok(filteredUsers);
    }

    // QueryParam 예시 - 페이지네이션
    @GetMapping("/page")
    public ResponseEntity<List<User>> getUsersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<User> allUsers = userService.getAllUsers();
        
        // 간단한 페이지네이션 (실제로는 Repository에서 처리)
        int start = page * size;
        int end = Math.min(start + size, allUsers.size());
        
        if (start >= allUsers.size()) {
            return ResponseEntity.ok(List.of());
        }
        
        List<User> pageUsers = allUsers.subList(start, end);
        return ResponseEntity.ok(pageUsers);
    }

    // PathVariable + QueryParam 조합 예시
    @GetMapping("/{id}/details")
    public ResponseEntity<String> getUserDetails(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeEmail,
            @RequestParam(defaultValue = "false") boolean includeAge) {
        
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User foundUser = user.get();
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(foundUser.getId()).append(", ");
        details.append("이름: ").append(foundUser.getName());
        
        if (includeEmail) {
            details.append(", 이메일: ").append(foundUser.getEmail());
        }
        
        if (includeAge) {
            details.append(", 나이: ").append(foundUser.getAge());
        }
        
        return ResponseEntity.ok(details.toString());
    }
} 