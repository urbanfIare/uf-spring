# UF Spring Boot Application

## 🐳 Docker로 PostgreSQL 실행하기

### 1. Docker 설치
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) 설치
- Docker Desktop 실행

### 2. PostgreSQL 컨테이너 시작
```bash
# Windows
start-db.bat

# 또는 직접 실행
docker-compose up -d
```

### 3. Spring Boot 애플리케이션 실행
```bash
./gradlew bootRun
```

### 4. PostgreSQL 컨테이너 중지
```bash
# Windows
stop-db.bat

# 또는 직접 실행
docker-compose down
```

## 📊 데이터베이스 정보
- **호스트**: localhost
- **포트**: 5432
- **데이터베이스**: ufdb
- **사용자**: postgres
- **비밀번호**: password

## 🔧 Docker 명령어
```bash
# 컨테이너 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs postgres

# 컨테이너 재시작
docker-compose restart postgres

# 데이터베이스 접속
docker exec -it uf-postgres psql -U postgres -d ufdb
```

## 🚀 테스트 계정
서버 시작 시 자동으로 생성되는 테스트 계정:
- **admin/admin** (관리자)
- **user/user** (일반 사용자)
- **test1@example.com/password123**
- **test2@example.com/password123**
- **test3@example.com/password123**

## 📝 API 문서
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs 