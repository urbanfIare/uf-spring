@echo off
echo ========================================
echo PostgreSQL Docker 컨테이너 시작
echo ========================================

echo Docker Compose로 PostgreSQL 시작 중...
docker-compose up -d

echo.
echo PostgreSQL이 시작되었습니다!
echo 데이터베이스 정보:
echo - 호스트: localhost
echo - 포트: 5432
echo - 데이터베이스: ufdb
echo - 사용자: postgres
echo - 비밀번호: password
echo.
echo Spring Boot 애플리케이션을 시작할 수 있습니다.
echo.
pause 