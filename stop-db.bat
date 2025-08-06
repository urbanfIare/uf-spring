@echo off
echo ========================================
echo PostgreSQL Docker 컨테이너 중지
echo ========================================

echo Docker Compose로 PostgreSQL 중지 중...
docker-compose down

echo.
echo PostgreSQL이 중지되었습니다!
echo.
pause 