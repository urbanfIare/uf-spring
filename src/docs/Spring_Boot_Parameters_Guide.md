# Spring Boot 파라미터 처리 가이드

## 📋 목차
1. [개요](#개요)
2. [@PathVariable](#pathvariable)
3. [@RequestParam](#requestparam)
4. [@RequestBody](#requestbody)
5. [실제 사용 예시](#실제-사용-예시)
6. [언제 어떤 것을 사용할까?](#언제-어떤-것을-사용할까)
7. [조합 사용법](#조합-사용법)

---

## 개요

Spring Boot에서 HTTP 요청의 파라미터를 처리하는 방법은 크게 세 가지가 있습니다:

- **@PathVariable**: URL 경로에서 데이터 추출
- **@RequestParam**: URL 쿼리 파라미터에서 데이터 추출  
- **@RequestBody**: HTTP 요청 본문에서 데이터 추출

---

## @PathVariable

### 정의
URL 경로의 일부로 전달되는 파라미터를 추출하는 어노테이션입니다.

### 특징
- ✅ URL 경로에 직접 포함
- ✅ 리소스의 식별자로 사용
- ✅ 필수 값 (없으면 404 에러)
- ✅ 주로 GET, PUT, DELETE 요청에서 사용

### 문법
```java
@GetMapping("/{id}")
public ResponseEntity<User> getUserById(@PathVariable Long id) {
    // id는 URL 경로에서 추출됨
}
```

### URL 예시
```
GET /api/users/123
→ id = 123

GET /api/users/email/kim@example.com  
→ email = "kim@example.com"

GET /api/users/age/25
→ age = 25
```

### 실제 코드 예시
```java
// ID로 사용자 조회
@GetMapping("/{id}")
public ResponseEntity<User> getUserById(@PathVariable Long id) {
    Optional<User> user = userService.getUserById(id);
    return user.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

// 이메일로 사용자 조회
@GetMapping("/email/{email}")
public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    Optional<User> user = userService.getUserByEmail(email);
    return user.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

---

## @RequestParam

### 정의
URL 쿼리 파라미터에서 데이터를 추출하는 어노테이션입니다.

### 특징
- ✅ URL 뒤에 ?로 시작하는 파라미터
- ✅ 선택적 값 (required=false 가능)
- ✅ 검색 조건, 필터링, 페이지네이션에 사용
- ✅ 주로 GET 요청에서 사용

### 문법
```java
@GetMapping("/search")
public ResponseEntity<List<User>> searchUsers(
    @RequestParam(required = false) String name,
    @RequestParam(defaultValue = "0") int minAge) {
    // name과 minAge는 쿼리 파라미터에서 추출됨
}
```

### URL 예시
```
GET /api/users/search?name=김&email=kim&minAge=20&maxAge=35
→ name = "김", email = "kim", minAge = 20, maxAge = 35

GET /api/users/page?page=0&size=10
→ page = 0, size = 10

GET /api/users/search?name=김
→ name = "김", email = null, minAge = 0 (기본값)
```

### 실제 코드 예시
```java
// 검색 기능
@GetMapping("/search")
public ResponseEntity<List<User>> searchUsers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        @RequestParam(defaultValue = "0") int minAge,
        @RequestParam(defaultValue = "100") int maxAge) {
    
    List<User> allUsers = userService.getAllUsers();
    
    // 필터링 로직
    List<User> filteredUsers = allUsers.stream()
            .filter(user -> name == null || user.getName().contains(name))
            .filter(user -> email == null || user.getEmail().contains(email))
            .filter(user -> user.getAge() >= minAge && user.getAge() <= maxAge)
            .toList();
    
    return ResponseEntity.ok(filteredUsers);
}

// 페이지네이션
@GetMapping("/page")
public ResponseEntity<List<User>> getUsersWithPagination(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    
    List<User> allUsers = userService.getAllUsers();
    
    // 페이지네이션 로직
    int start = page * size;
    int end = Math.min(start + size, allUsers.size());
    
    if (start >= allUsers.size()) {
        return ResponseEntity.ok(List.of());
    }
    
    List<User> pageUsers = allUsers.subList(start, end);
    return ResponseEntity.ok(pageUsers);
}
```

---

## @RequestBody

### 정의
HTTP 요청 본문에서 JSON 데이터를 Java 객체로 변환하는 어노테이션입니다.

### 특징
- ✅ HTTP 요청 본문에 JSON 데이터
- ✅ 복잡한 객체 전송
- ✅ POST, PUT 요청에서 사용
- ✅ 필수 값

### 문법
```java
@PostMapping
public ResponseEntity<User> createUser(@RequestBody User user) {
    // JSON 데이터가 User 객체로 변환됨
}
```

### 요청 예시
```
POST /api/users
Content-Type: application/json

{
  "name": "홍길동",
  "email": "hong@example.com",
  "age": 30
}
```

### 실제 코드 예시
```java
// 새 사용자 생성
@PostMapping
public ResponseEntity<User> createUser(@RequestBody User user) {
    User createdUser = userService.createUser(user);
    return ResponseEntity.ok(createdUser);
}

// 사용자 정보 수정
@PutMapping("/{id}")
public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
    Optional<User> updatedUser = userService.updateUser(id, userDetails);
    return updatedUser.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

---

## 실제 사용 예시

### 1. PathVariable + RequestParam 조합
```java
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
```

**요청 예시:**
```
GET /api/users/123/details?includeEmail=true&includeAge=false
→ id = 123 (PathVariable), includeEmail = true, includeAge = false (RequestParam)
```

### 2. RequestBody + PathVariable 조합
```java
@PutMapping("/{id}")
public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
    Optional<User> updatedUser = userService.updateUser(id, userDetails);
    return updatedUser.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

**요청 예시:**
```
PUT /api/users/123
Content-Type: application/json

{
  "name": "홍길동",
  "email": "hong@example.com",
  "age": 30
}
→ id = 123 (PathVariable), userDetails = User 객체 (RequestBody)
```

---

## 언제 어떤 것을 사용할까?

### @PathVariable 사용 시기
- ✅ **리소스 식별** (ID, 이메일 등)
- ✅ **URL 경로의 일부**가 되는 값
- ✅ **필수 값**이어야 할 때
- ✅ **단순한 값** (String, Long, int 등)

**예시:**
```java
GET /api/users/123          // 사용자 ID
GET /api/products/ABC123    // 상품 코드
GET /api/categories/electronics  // 카테고리명
```

### @RequestParam 사용 시기
- ✅ **검색 조건** (이름, 이메일 등)
- ✅ **필터링** (나이 범위, 가격 범위 등)
- ✅ **페이지네이션** (페이지 번호, 크기)
- ✅ **선택적 값**이어야 할 때

**예시:**
```java
GET /api/users/search?name=김&age=25&page=0&size=10
GET /api/products?category=electronics&minPrice=1000&maxPrice=50000
GET /api/orders?status=pending&startDate=2024-01-01&endDate=2024-12-31
```

### @RequestBody 사용 시기
- ✅ **데이터 생성** (새 사용자, 새 상품 등)
- ✅ **데이터 수정** (사용자 정보 업데이트)
- ✅ **복잡한 객체** 전송
- ✅ **여러 필드**가 필요할 때

**예시:**
```java
POST /api/users
{
  "name": "홍길동",
  "email": "hong@example.com",
  "age": 30,
  "address": {
    "city": "서울",
    "street": "강남대로"
  }
}
```

---

## 조합 사용법

### 1. PathVariable + RequestParam
```java
@GetMapping("/{id}/details")
public ResponseEntity<String> getUserDetails(
        @PathVariable Long id,
        @RequestParam(defaultValue = "false") boolean includeEmail) {
    // id는 경로에서, includeEmail은 쿼리에서
}
```

### 2. PathVariable + RequestBody
```java
@PutMapping("/{id}")
public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    // id는 경로에서, user는 본문에서
}
```

### 3. RequestParam + RequestBody (드물게 사용)
```java
@PostMapping("/users")
public ResponseEntity<User> createUser(
        @RequestParam String type,
        @RequestBody User user) {
    // type은 쿼리에서, user는 본문에서
}
```

---

## 📊 비교표

| 구분 | @PathVariable | @RequestParam | @RequestBody |
|------|---------------|---------------|--------------|
| **위치** | URL 경로 | URL 쿼리 | HTTP 본문 |
| **형태** | `/users/123` | `?name=김&age=25` | `{"name":"김"}` |
| **용도** | 리소스 식별 | 검색/필터링 | 데이터 생성/수정 |
| **필수성** | 필수 | 선택적 | 필수 |
| **HTTP 메서드** | GET, PUT, DELETE | GET | POST, PUT |
| **데이터 타입** | 단순 값 | 단순 값 | 복잡한 객체 |
| **예시** | `/api/users/123` | `?name=김&age=25` | `{"name":"김","age":25}` |

---

## 🎯 핵심 포인트

1. **@PathVariable**: URL 경로의 일부, 리소스 식별자
2. **@RequestParam**: 검색 조건, 필터링, 선택적 값
3. **@RequestBody**: 복잡한 데이터, POST/PUT 요청
4. **조합 사용**: 상황에 맞게 적절히 조합하여 사용

이렇게 세 가지 방식을 상황에 맞게 적절히 사용하면 RESTful API를 효과적으로 구현할 수 있습니다! 🚀 