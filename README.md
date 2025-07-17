# Spring Boot 프로젝트 문서

이 폴더는 Spring Boot 프로젝트와 관련된 모든 문서들을 포함합니다.

## 📚 문서 목록

### 1. [Spring Boot 파라미터 처리 가이드](./src/docs/Spring_Boot_Parameters_Guide.md)
- @PathVariable, @RequestParam, @RequestBody의 차이점과 사용법
- 실제 코드 예시와 URL 형태
- 언제 어떤 것을 사용할까?

### 2. [프로젝트 구조 가이드](./src/docs/Project_Structure_Guide.md)
- Spring Boot 프로젝트의 전체 아키텍처
- 각 계층별 역할과 책임
- 패키지 구조 설명

### 3. [API 엔드포인트 문서](./src/docs/API_Endpoints.md)
- 모든 REST API 엔드포인트 목록
- 요청/응답 예시
- 테스트 방법

### 4. [데이터베이스 설정 가이드](./src/docs/Database_Setup_Guide.md)
- H2 Database 설정 방법
- JPA 엔티티 설계
- MySQL로 변경하는 방법

### 5. [Spring Security 가이드](./src/docs/Spring_Security_Guide.md)
- 사용자 인증 및 권한 관리
- 비밀번호 암호화 (BCrypt)
- 역할 기반 접근 제어
- API 보안 설정

### 6. [무한 리다이렉트 문제 해결 가이드](./src/docs/Spring_Security_Redirect_Issue_Guide.md)
- ERR_TOO_MANY_REDIRECTS 오류 해결
- REST API vs 웹 애플리케이션 설정
- 인증 방식 선택 가이드

## 🚀 빠른 시작

1. **프로젝트 실행**: `gradlew.bat bootRun`
2. **API 테스트**: `http://localhost:8080/test.html`
3. **H2 콘솔**: `http://localhost:8080/h2-console`

## 📁 프로젝트 구조

```
src/
├── docs/                    # 📚 문서 폴더
│   ├── README.md           # 이 파일
│   ├── Spring_Boot_Parameters_Guide.md
│   ├── Project_Structure_Guide.md
│   ├── API_Endpoints.md
│   ├── Database_Setup_Guide.md
│   ├── Spring_Security_Guide.md
│   └── Spring_Security_Redirect_Issue_Guide.md
├── main/
│   ├── java/
│   │   └── com/example/uf_spring/
│   │       ├── controller/     # REST API 컨트롤러
│   │       ├── model/          # 엔티티 모델
│   │       ├── repository/     # 데이터 접근 계층
│   │       ├── service/        # 비즈니스 로직
│   │       └── config/         # 설정 클래스
│   └── resources/
│       ├── static/             # 정적 파일 (HTML, CSS, JS)
│       └── application.properties
└── test/                      # 테스트 코드
```

## 🛠️ 기술 스택

- **Spring Boot 3.5.3**: 메인 프레임워크
- **Spring Security**: 인증 및 권한 관리
- **Spring Data JPA**: 데이터 접근 계층
- **H2 Database**: 파일 기반 데이터베이스
- **Hibernate**: ORM (Object-Relational Mapping)
- **BCrypt**: 비밀번호 암호화
- **Lombok**: 보일러플레이트 코드 감소
- **Gradle**: 빌드 도구

## 📝 문서 작성 가이드

새로운 문서를 추가할 때는 다음 형식을 따르세요:

1. **파일명**: `주제_가이드.md` (영어, 언더스코어 사용)
2. **목차**: 문서 상단에 목차 포함
3. **예시**: 실제 코드 예시 포함
4. **이모지**: 가독성을 위해 적절한 이모지 사용

---

💡 **팁**: 이 문서들은 프로젝트 개발 과정에서 지속적으로 업데이트됩니다! 