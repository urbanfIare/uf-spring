package com.example.uf_spring.controller;

import com.example.uf_spring.model.User;
import com.example.uf_spring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 관리", description = "사용자 CRUD 및 검색 API")
public class UserController {

    private final UserService userService;

    // 모든 사용자 조회
    @GetMapping
    @Operation(summary = "모든 사용자 조회", description = "시스템에 등록된 모든 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // ID로 사용자 조회 (PathVariable 예시)
    @GetMapping("/{id}")
    @Operation(summary = "사용자 ID로 조회", description = "사용자 ID를 통해 특정 사용자 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 새 사용자 생성 (RequestBody 예시)
    @PostMapping
    @Operation(summary = "새 사용자 생성", description = "새로운 사용자를 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<User> createUser(
            @Parameter(description = "생성할 사용자 정보", required = true) @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // 사용자 정보 수정 (PathVariable + RequestBody 조합)
    @PutMapping("/{id}")
    @Operation(summary = "사용자 정보 수정", description = "기존 사용자의 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 수정 성공"),
        @ApiResponse(responseCode = "404", description = "수정할 사용자를 찾을 수 없음")
    })
    public ResponseEntity<User> updateUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long id,
            @Parameter(description = "수정할 사용자 정보", required = true) @RequestBody User userDetails) {
        Optional<User> updatedUser = userService.updateUser(id, userDetails);
        return updatedUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 사용자 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "사용자 삭제", description = "특정 사용자를 시스템에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "삭제할 사용자를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "삭제할 사용자 ID", required = true) @PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 이메일로 사용자 조회 (PathVariable)
    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 사용자 조회", description = "이메일 주소로 사용자를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "검색할 이메일 주소", required = true) @PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 나이로 사용자 조회 (PathVariable)
    @GetMapping("/age/{age}")
    @Operation(summary = "나이로 사용자 조회", description = "특정 나이 이상의 사용자들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    })
    public ResponseEntity<List<User>> getUsersByAgeGreaterThan(
            @Parameter(description = "최소 나이", required = true) @PathVariable int age) {
        List<User> users = userService.getUsersByAgeGreaterThan(age);
        return ResponseEntity.ok(users);
    }

    // ===== 새로운 예시들 =====
    
    // QueryParam 예시 - 검색 기능
    @GetMapping("/search")
    @Operation(summary = "사용자 검색", description = "다양한 조건으로 사용자를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "검색 결과 반환")
    })
    public ResponseEntity<List<User>> searchUsers(
            @Parameter(description = "이름 검색어") @RequestParam(required = false) String name,
            @Parameter(description = "이메일 검색어") @RequestParam(required = false) String email,
            @Parameter(description = "최소 나이", example = "0") @RequestParam(defaultValue = "0") int minAge,
            @Parameter(description = "최대 나이", example = "100") @RequestParam(defaultValue = "100") int maxAge) {
        
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
    @Operation(summary = "사용자 페이지네이션", description = "페이지 단위로 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "페이지 조회 성공")
    })
    public ResponseEntity<List<User>> getUsersWithPagination(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {
        
        List<User> allUsers = userService.getAllUsers();
        
        // 간단한 페이지네이션 로직
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
    @Operation(summary = "사용자 상세 정보", description = "사용자의 상세 정보를 선택적으로 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상세 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<String> getUserDetails(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long id,
            @Parameter(description = "이메일 포함 여부", example = "false") @RequestParam(defaultValue = "false") boolean includeEmail,
            @Parameter(description = "나이 포함 여부", example = "false") @RequestParam(defaultValue = "false") boolean includeAge) {
        
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(user.getId()).append(", 이름: ").append(user.getName());
        
        if (includeEmail) {
            details.append(", 이메일: ").append(user.getEmail());
        }
        
        if (includeAge) {
            details.append(", 나이: ").append(user.getAge());
        }
        
        return ResponseEntity.ok(details.toString());
    }
} 