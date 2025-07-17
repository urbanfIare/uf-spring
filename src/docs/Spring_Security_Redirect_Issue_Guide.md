# Spring Security 무한 리다이렉트 문제 해결 가이드

## 📋 목차
1. [문제 개요](#문제-개요)
2. [원인 분석](#원인-분석)
3. [해결 방법](#해결-방법)
4. [REST API vs 웹 애플리케이션](#rest-api-vs-웹-애플리케이션)
5. [테스트 방법](#테스트-방법)
6. [예방 방법](#예방-방법)
7. [추가 설정](#추가-설정)

---

## 문제 개요

### 🚨 오류 메시지
```
ERR_TOO_MANY_REDIRECTS
localhost에서 리디렉션한 횟수가 너무 많습니다.
```

### 🔍 문제 현상
- 브라우저에서 Spring Boot 애플리케이션 접속 시 무한 리다이렉트 발생
- 로그인 페이지로 계속 리다이렉트되지만 로그인해도 다시 리다이렉트
- 쿠키 삭제해도 문제가 해결되지 않음

---

## 원인 분석

### 1. 주요 원인

#### A. REST API에서 formLogin 사용
```java
// 문제가 되는 설정
.formLogin(form -> form
    .loginPage("/login")
    .permitAll()
)
```

**문제점**:
- REST API는 JSON 응답을 사용하는데, 브라우저 기반 로그인 폼을 요구
- `/login` 페이지가 실제로 존재하지 않거나 인증이 필요한 상태
- 인증이 필요한 URL → `/login` 리다이렉트 → `/login`도 인증 필요 → 무한 루프

#### B. 로그인 페이지 경로 설정 오류
```java
// 잘못된 설정 예시
.requestMatchers("/login").authenticated() // 로그인 페이지도 인증 필요로 설정
```

#### C. 세션 관리 문제
- 세션이 제대로 생성되지 않거나 만료됨
- CSRF 토큰 문제
- 쿠키 설정 문제

### 2. 발생 시나리오

```
1. 사용자가 /api/users 접근
2. Spring Security: 인증 필요 → /login으로 리다이렉트
3. /login 페이지도 인증 필요로 설정됨
4. 다시 /login으로 리다이렉트
5. 무한 루프 발생
```

---

## 해결 방법

### 1. REST API용 설정 (권장)

#### A. formLogin 제거 + httpBasic 사용
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/auth/**", "/api/hello", "/test.html", "/h2-console/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**")
        )
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.sameOrigin())
        )
        .httpBasic(); // HTTP Basic 인증만 사용

    return http.build();
}
```

#### B. JWT 토큰 인증 사용
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .csrf(csrf -> csrf.disable()) // REST API에서는 CSRF 비활성화
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

### 2. 웹 애플리케이션용 설정

#### A. 로그인 페이지 컨트롤러 생성
```java
@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String login() {
        return "login"; // login.html 템플릿 반환
    }
    
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
```

#### B. SecurityConfig 수정
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/login", "/css/**", "/js/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/login")
            .permitAll()
        );

    return http.build();
}
```

### 3. 즉시 해결 방법

#### A. 브라우저 캐시/쿠키 삭제
1. 브라우저 개발자 도구 열기 (F12)
2. Application 탭 → Storage → Clear storage
3. 모든 쿠키와 캐시 삭제

#### B. 시크릿 모드에서 테스트
- 일반 모드에서 문제가 발생하면 시크릿 모드로 테스트

#### C. 서버 재시작
```bash
# 서버 중지 후 재시작
./gradlew bootRun
```

---

## REST API vs 웹 애플리케이션

### REST API (권장)
```java
// ✅ REST API용 설정
.httpBasic() // HTTP Basic 인증
// 또는
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

**장점**:
- JSON 응답에 적합
- 클라이언트-서버 분리
- 모바일 앱, SPA 등에 적합

### 웹 애플리케이션
```java
// ✅ 웹 애플리케이션용 설정
.formLogin(form -> form
    .loginPage("/login")
    .permitAll()
)
```

**장점**:
- 전통적인 웹 페이지
- 서버 사이드 렌더링
- 브라우저 기반 인증

---

## 테스트 방법

### 1. HTTP Basic 인증 테스트

#### A. curl 명령어
```bash
# 인증 없이 접근 (401 Unauthorized 예상)
curl -X GET http://localhost:8080/api/users

# 인증과 함께 접근
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Basic dGVzdEBleGFtcGxlLmNvbTpwYXNzd29yZDEyMw=="
```

#### B. 브라우저 테스트
1. `http://localhost:8080/api/users` 접속
2. 브라우저 팝업에서 인증 정보 입력
3. 정상 접근 확인

### 2. 공개 엔드포인트 테스트
```bash
# 인증 없이 접근 가능해야 함
curl -X GET http://localhost:8080/api/hello
curl -X GET http://localhost:8080/api/auth/status
```

### 3. 역할별 접근 테스트
```bash
# ADMIN 역할로 접근
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Basic YWRtaW5AZXhhbXBsZS5jb206cGFzc3dvcmQxMjM="
```

---

## 예방 방법

### 1. 설정 검증 체크리스트

- [ ] REST API인지 웹 애플리케이션인지 명확히 구분
- [ ] 로그인 페이지 경로가 실제로 존재하는지 확인
- [ ] 로그인 페이지가 permitAll()로 설정되었는지 확인
- [ ] 세션 설정이 적절한지 확인

### 2. 개발 단계별 확인

#### A. 초기 설정
```java
// 1단계: 모든 요청 허용 (테스트용)
.authorizeHttpRequests(authz -> authz
    .anyRequest().permitAll()
)
```

#### B. 보안 설정 추가
```java
// 2단계: 인증 추가
.authorizeHttpRequests(authz -> authz
    .requestMatchers("/api/auth/**").permitAll()
    .anyRequest().authenticated()
)
```

#### C. 역할 기반 접근 제어
```java
// 3단계: 역할별 접근 제어
.authorizeHttpRequests(authz -> authz
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```

### 3. 디버깅 방법

#### A. 로그 확인
```properties
# application.properties
logging.level.org.springframework.security=DEBUG
logging.level.com.example.uf_spring=DEBUG
```

#### B. 브라우저 개발자 도구
1. Network 탭에서 리다이렉트 체인 확인
2. Response 헤더에서 Location 확인
3. 쿠키 상태 확인

---

## 추가 설정

### 1. 세션 관리

#### A. 세션 타임아웃 설정
```properties
# application.properties
server.servlet.session.timeout=30m
```

#### B. 세션 생성 정책
```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
)
```

### 2. CSRF 설정

#### A. REST API에서 CSRF 비활성화
```java
.csrf(csrf -> csrf.disable())
```

#### B. 특정 경로만 CSRF 비활성화
```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers("/api/**", "/h2-console/**")
)
```

### 3. 헤더 설정

#### A. 보안 헤더 설정
```java
.headers(headers -> headers
    .frameOptions(frameOptions -> frameOptions.sameOrigin())
    .contentTypeOptions(contentType -> {})
    .httpStrictTransportSecurity(hsts -> hsts
        .includeSubdomains(true)
        .maxAgeInSeconds(31536000)
    )
)
```

---

## 🎯 핵심 포인트

1. **REST API는 httpBasic() 사용**: formLogin() 대신 HTTP Basic 인증
2. **로그인 페이지 경로 확인**: 실제로 존재하고 permitAll() 설정
3. **브라우저 캐시/쿠키 삭제**: 즉시 해결 방법
4. **서버 재시작**: 설정 변경 후 필수
5. **단계별 설정**: 점진적으로 보안 설정 추가

---

## 📚 참고 자료

- [Spring Security 공식 문서](https://docs.spring.io/spring-security/reference/)
- [Spring Boot Security 가이드](https://spring.io/guides/gs/securing-web/)
- [HTTP Basic Authentication](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication)

이 가이드를 통해 무한 리다이렉트 문제를 해결하고, 앞으로 같은 문제가 발생하지 않도록 예방할 수 있습니다! 🚀 