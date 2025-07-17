# Spring Security 가이드

## 📋 목차
1. [개요](#개요)
2. [Spring Security 설정](#spring-security-설정)
3. [인증 및 권한 관리](#인증-및-권한-관리)
4. [비밀번호 암호화](#비밀번호-암호화)
5. [사용자 역할 시스템](#사용자-역할-시스템)
6. [API 보안 설정](#api-보안-설정)
7. [테스트 방법](#테스트-방법)
8. [문제 해결](#문제-해결)

---

## 개요

이 프로젝트는 Spring Security를 사용하여 사용자 인증과 권한 관리를 구현했습니다. 주요 기능은 다음과 같습니다:

- 🔐 **사용자 인증**: 이메일/비밀번호 기반 로그인
- 🔒 **권한 관리**: USER, MODERATOR, ADMIN 역할 시스템
- 🔑 **비밀번호 암호화**: BCrypt를 사용한 안전한 비밀번호 저장
- 🛡️ **API 보안**: 역할 기반 접근 제어
- 📝 **회원가입**: 새로운 사용자 등록 기능

---

## Spring Security 설정

### 1. 의존성 추가

`build.gradle`에 Spring Security 의존성을 추가했습니다:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
```

### 2. SecurityConfig 클래스

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
        return customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // 공개 엔드포인트
                .requestMatchers("/api/auth/**", "/api/hello", "/test.html", "/h2-console/**").permitAll()
                // 관리자 전용
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 인증된 사용자만
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );

        return http.build();
    }
}
```

### 3. 주요 설정 설명

| 설정 | 설명 |
|------|------|
| `@EnableWebSecurity` | Spring Security 활성화 |
| `PasswordEncoder` | BCrypt 비밀번호 암호화 |
| `UserDetailsService` | 사용자 정보 로드 서비스 |
| `SecurityFilterChain` | HTTP 보안 필터 체인 |

---

## 인증 및 권한 관리

### 1. CustomUserDetailsService

사용자 정보를 데이터베이스에서 로드하는 서비스입니다:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
```

### 2. AuthService

회원가입과 로그인을 처리하는 서비스입니다:

```java
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원가입
    public User register(RegisterRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 새 사용자 생성
        User newUser = new User(
            request.getName(),
            request.getEmail(),
            encodedPassword,
            request.getAge(),
            Role.USER // 기본 역할
        );

        return userRepository.save(newUser);
    }

    // 비밀번호 검증
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

---

## 비밀번호 암호화

### 1. BCrypt 암호화

Spring Security는 BCrypt 알고리즘을 사용하여 비밀번호를 안전하게 암호화합니다:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 2. 암호화 과정

1. **회원가입 시**: `passwordEncoder.encode(rawPassword)`
2. **로그인 시**: `passwordEncoder.matches(rawPassword, encodedPassword)`

### 3. BCrypt의 장점

- ✅ **솔트 자동 생성**: 같은 비밀번호도 다른 해시값 생성
- ✅ **적응형 알고리즘**: 컴퓨팅 파워에 따라 강도 조절
- ✅ **안전성**: 현재 가장 안전한 비밀번호 해시 알고리즘

---

## 사용자 역할 시스템

### 1. Role Enum

```java
public enum Role {
    USER,      // 일반 사용자
    MODERATOR, // 중간 관리자
    ADMIN      // 최고 관리자
}
```

### 2. User 엔티티

```java
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private int age;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER; // 기본값은 USER
}
```

### 3. 역할별 권한

| 역할 | 권한 | 설명 |
|------|------|------|
| `USER` | 기본 접근 | 일반 사용자 기능 |
| `MODERATOR` | 중간 관리 | 사용자 관리, 콘텐츠 관리 |
| `ADMIN` | 전체 관리 | 모든 기능 접근 가능 |

---

## API 보안 설정

### 1. URL 패턴별 접근 제어

```java
.authorizeHttpRequests(authz -> authz
    // 공개 엔드포인트 (인증 불필요)
    .requestMatchers("/api/auth/**", "/api/hello", "/test.html", "/h2-console/**").permitAll()
    // 관리자 전용
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    // 인증된 사용자만
    .anyRequest().authenticated()
)
```

### 2. 보안 설정 상세

| URL 패턴 | 접근 권한 | 설명 |
|----------|-----------|------|
| `/api/auth/**` | 모든 사용자 | 회원가입, 로그인 API |
| `/api/hello` | 모든 사용자 | 테스트용 API |
| `/test.html` | 모든 사용자 | 테스트 페이지 |
| `/h2-console/**` | 모든 사용자 | H2 데이터베이스 콘솔 |
| `/api/admin/**` | ADMIN 역할만 | 관리자 전용 API |
| 기타 모든 URL | 인증된 사용자만 | 일반 사용자 API |

### 3. CSRF 보호

```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers("/h2-console/**")
)
```

H2 콘솔을 위한 CSRF 보호 비활성화 설정입니다.

---

## 테스트 방법

### 1. 회원가입 테스트

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "테스트 사용자",
    "email": "test@example.com",
    "password": "password123",
    "age": 25
  }'
```

### 2. 로그인 테스트

Spring Security의 기본 폼 로그인을 사용합니다:

1. 브라우저에서 `http://localhost:8080/login` 접속
2. 이메일과 비밀번호 입력
3. 로그인 성공 시 메인 페이지로 리다이렉트

### 3. API 접근 테스트

```bash
# 인증이 필요한 API 테스트
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Basic dGVzdEBleGFtcGxlLmNvbTpwYXNzd29yZDEyMw=="
```

### 4. 역할별 접근 테스트

```bash
# 관리자 전용 API 테스트 (ADMIN 역할 필요)
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Basic YWRtaW5AZXhhbXBsZS5jb206cGFzc3dvcmQxMjM="
```

---

## 문제 해결

### 1. 순환 의존성 문제

**문제**: SecurityConfig와 CustomUserDetailsService 간의 순환 의존성

**해결**: UserDetailsService를 별도 Bean으로 분리

```java
@Bean
public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
    return customUserDetailsService;
}
```

### 2. 데이터베이스 스키마 업데이트

**문제**: 새로운 컬럼(password, role) 추가 시 null 제약 조건 오류

**해결**: 
1. H2 데이터베이스 파일 삭제
2. 애플리케이션 재시작으로 스키마 재생성

### 3. Spring Security 클래스 누락

**문제**: IDE에서 Spring Security 클래스를 찾을 수 없음

**해결**:
1. Gradle 의존성 새로고침
2. IDE 프로젝트 리로드
3. 프로젝트 재빌드

### 4. 로그인 실패

**문제**: 사용자 인증 실패

**해결**:
1. 사용자가 데이터베이스에 존재하는지 확인
2. 비밀번호가 올바른지 확인
3. 이메일을 사용자명으로 사용하는지 확인

---

## 🔧 추가 설정 옵션

### 1. JWT 토큰 인증

현재는 세션 기반 인증을 사용하지만, JWT 토큰으로 변경 가능:

```java
// JWT 의존성 추가
implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
implementation 'io.jsonwebtoken:jjwt-impl:0.11.5'
implementation 'io.jsonwebtoken:jjwt-jackson:0.11.5'
```

### 2. OAuth2 소셜 로그인

Google, Facebook 등 소셜 로그인 추가:

```java
// OAuth2 의존성 추가
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
```

### 3. 로그아웃 설정

```java
.logout(logout -> logout
    .logoutUrl("/api/auth/logout")
    .logoutSuccessUrl("/")
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID")
)
```

---

## 📚 학습 리소스

1. **Spring Security 공식 문서**: https://docs.spring.io/spring-security/reference/
2. **Spring Boot Security 가이드**: https://spring.io/guides/gs/securing-web/
3. **BCrypt 알고리즘**: https://en.wikipedia.org/wiki/Bcrypt

---

## 🎯 핵심 포인트

1. **보안 우선**: 모든 API는 기본적으로 보호됨
2. **역할 기반 접근**: ADMIN, MODERATOR, USER 역할 시스템
3. **안전한 비밀번호**: BCrypt 암호화로 비밀번호 보호
4. **유연한 설정**: URL 패턴별 세밀한 접근 제어
5. **확장 가능**: JWT, OAuth2 등 추가 인증 방식 지원

이러한 Spring Security 설정으로 안전하고 확장 가능한 웹 애플리케이션을 구축할 수 있습니다! 🚀 