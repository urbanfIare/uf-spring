-- 테스트 유저 데이터 초기화 (2개만)
-- admin 유저 (관리자) - admin/admin
INSERT INTO users (id, name, email, password, age, role) 
VALUES (1, '관리자', 'admin', '$2a$10$dXJ3SW6G7P50lGmrMBSorO3v3rgoj1iZrxg3VWlizxnC9wQAYQi1G', 30, 'ADMIN');

-- user 유저 (일반 사용자) - user/user
INSERT INTO users (id, name, email, password, age, role) 
VALUES (2, '테스트유저', 'user', '$2a$10$dXJ3SW6G7P50lGmrMBSorO3v3rgoj1iZrxg3VWlizxnC9wQAYQi1G', 25, 'USER'); 