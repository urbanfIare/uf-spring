# Spring Boot 프로젝트 구조 가이드

## 📋 목차
1. [개요](#개요)
2. [전체 아키텍처](#전체-아키텍처)
3. [계층별 역할](#계층별-역할)
4. [패키지 구조](#패키지-구조)
5. [데이터 흐름](#데이터-흐름)
6. [설계 원칙](#설계-원칙)

---

## 개요

이 문서는 Spring Boot 프로젝트의 전체 구조와 각 계층의 역할을 설명합니다.

### 🏗️ 아키텍처 패턴
- **Layered Architecture**: 계층별로 역할을 분리
- **RESTful API**: 표준 HTTP 메서드 사용
- **Repository Pattern**: 데이터 접근 계층 분리
- **Service Layer**: 비즈니스 로직 분리

---

## 전체 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                    Client (브라우저/앱)                      │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP Request/Response
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                Controller Layer (표현 계층)                  │
│  - @RestController, @RequestMapping                        │
│  - HTTP 요청 처리 및 응답 생성                              │
└─────────────────────┬───────────────────────────────────────┘
                      │ Service 호출
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                Service Layer (비즈니스 계층)                 │
│  - @Service                                              │
│  - 비즈니스 로직 처리                                      │
│  - Repository 조합                                        │
└─────────────────────┬───────────────────────────────────────┘
                      │ Repository 호출
                      ▼
┌─────────────────────────────────────────────────────────────┐
│              Repository Layer (데이터 접근 계층)             │
│  - @Repository                                           │
│  - JpaRepository 상속                                    │
│  - 데이터베이스 CRUD 작업                                  │
└─────────────────────┬───────────────────────────────────────┘
                      │ SQL Query
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer                          │
│  - H2 Database (개발용)                                   │
│  - MySQL/PostgreSQL (운영용)                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 계층별 역할

### 1. Controller Layer (표현 계층)

**역할**: HTTP 요청을 받아서 적절한 응답을 반환

**주요 어노테이션**:
- `@RestController`: REST API 컨트롤러
- `@RequestMapping`: 기본 URL 경로
- `@GetMapping`, `@PostMapping` 등: HTTP 메서드 매핑

**예시**:
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
```

**책임**:
- ✅ HTTP 요청/응답 처리
- ✅ 파라미터 검증
- ✅ 적절한 HTTP 상태 코드 반환
- ✅ JSON 직렬화/역직렬화

### 2. Service Layer (비즈니스 계층)

**역할**: 비즈니스 로직 처리 및 Repository 조합

**주요 어노테이션**:
- `@Service`: 서비스 계층임을 명시
- `@Autowired`: 의존성 주입

**예시**:
```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
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
}
```

**책임**:
- ✅ 비즈니스 로직 처리
- ✅ 트랜잭션 관리
- ✅ Repository 조합
- ✅ 데이터 검증

### 3. Repository Layer (데이터 접근 계층)

**역할**: 데이터베이스와의 직접적인 상호작용

**주요 어노테이션**:
- `@Repository`: 데이터 접근 계층임을 명시
- `JpaRepository<User, Long>`: 기본 CRUD 기능 상속

**예시**:
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);
    
    // 나이로 사용자 찾기
    List<User> findByAgeGreaterThanEqual(int age);
}
```

**책임**:
- ✅ 데이터베이스 CRUD 작업
- ✅ 쿼리 메서드 정의
- ✅ 데이터 매핑

### 4. Model Layer (데이터 모델)

**역할**: 데이터베이스 테이블과 매핑되는 엔티티

**주요 어노테이션**:
- `@Entity`: JPA 엔티티
- `@Table`: 테이블명 지정
- `@Id`, `@GeneratedValue`: 기본키 설정
- `@Column`: 컬럼 속성

**예시**:
```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private int age;
}
```

**책임**:
- ✅ 데이터베이스 스키마 정의
- ✅ 데이터 검증 규칙
- ✅ 객체-관계 매핑

---

## 패키지 구조

```
src/main/java/com/example/uf_spring/
├── UfSpringApplication.java          # 메인 애플리케이션
├── controller/                       # REST API 컨트롤러
│   ├── HelloController.java         # 간단한 Hello API
│   └── UserController.java          # 사용자 CRUD API
├── model/                           # 데이터 모델
│   └── User.java                    # 사용자 엔티티
├── repository/                      # 데이터 접근 계층
│   └── UserRepository.java          # JPA Repository
├── service/                         # 비즈니스 로직
│   └── UserService.java             # 사용자 서비스
└── config/                          # 설정
    └── DataInitializer.java         # 초기 데이터 설정
```

### 패키지별 역할

| 패키지 | 역할 | 주요 클래스 |
|--------|------|-------------|
| `controller` | HTTP 요청 처리 | `UserController`, `HelloController` |
| `model` | 데이터 모델 | `User` 엔티티 |
| `repository` | 데이터 접근 | `UserRepository` |
| `service` | 비즈니스 로직 | `UserService` |
| `config` | 설정 및 초기화 | `DataInitializer` |

---

## 데이터 흐름

### 1. 사용자 조회 과정
```
1. Client: GET /api/users/123
2. Controller: @GetMapping("/{id}") → userService.getUserById(id)
3. Service: 비즈니스 로직 → userRepository.findById(id)
4. Repository: JpaRepository.findById() → Hibernate
5. Database: SELECT * FROM users WHERE id = 123
6. Response: User 객체를 JSON으로 반환
```

### 2. 사용자 생성 과정
```
1. Client: POST /api/users + JSON 데이터
2. Controller: @PostMapping → userService.createUser(user)
3. Service: 비즈니스 로직 → userRepository.save(user)
4. Repository: JpaRepository.save() → Hibernate
5. Database: INSERT INTO users (name, email, age) VALUES (...)
6. Response: 생성된 User 객체를 JSON으로 반환
```

### 3. 사용자 수정 과정
```
1. Client: PUT /api/users/123 + JSON 데이터
2. Controller: @PutMapping("/{id}") → userService.updateUser(id, userDetails)
3. Service: 기존 사용자 조회 → 데이터 수정 → 저장
4. Repository: findById() → save()
5. Database: UPDATE users SET name=?, email=?, age=? WHERE id=123
6. Response: 수정된 User 객체를 JSON으로 반환
```

---

## 설계 원칙

### 1. 단일 책임 원칙 (SRP)
- 각 클래스는 하나의 책임만 가짐
- Controller: HTTP 요청 처리
- Service: 비즈니스 로직
- Repository: 데이터 접근

### 2. 의존성 역전 원칙 (DIP)
- 상위 계층이 하위 계층에 의존하지 않음
- 인터페이스를 통한 의존성 주입

### 3. 관심사의 분리 (SoC)
- 각 계층은 자신의 역할에만 집중
- 계층 간 결합도 최소화

### 4. RESTful API 설계
- 표준 HTTP 메서드 사용
- 리소스 중심의 URL 설계
- 적절한 HTTP 상태 코드 사용

---

## 🎯 핵심 포인트

1. **계층 분리**: 각 계층은 명확한 역할과 책임을 가짐
2. **의존성 주입**: `@Autowired`를 통한 느슨한 결합
3. **인터페이스 활용**: 확장성과 유지보수성 향상
4. **표준 패턴**: Spring Boot의 표준 패턴 활용

이러한 구조로 설계하면 유지보수가 쉽고 확장 가능한 애플리케이션을 만들 수 있습니다! 🚀 