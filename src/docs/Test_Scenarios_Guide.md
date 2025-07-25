# 🧪 게시판 강화 기능 테스트 시나리오

## 📋 테스트 개요

이 문서는 Spring Boot 게시판의 강화된 기능들을 체계적으로 테스트하기 위한 시나리오입니다.

---

## 🚀 테스트 환경 설정

### 1. 애플리케이션 실행
```bash
gradlew.bat bootRun
```

### 2. 접속 URL들
- **고급 게시판**: `http://localhost:8080/board-enhanced.html`
- **고급 API 테스트**: `http://localhost:8080/api-enhanced.html`
- **기본 API 테스트**: `http://localhost:8080/test.html`
- **H2 데이터베이스 콘솔**: `http://localhost:8080/h2-console`

---

## 🧪 테스트 시나리오

### 시나리오 1: 기본 게시판 기능 테스트

#### 1.1 게시글 작성 테스트
**목표**: 게시글 생성 기능이 정상 작동하는지 확인

**단계**:
1. `http://localhost:8080/board-enhanced.html` 접속
2. "✏️ 새 글 작성" 버튼 클릭
3. 다음 정보 입력:
   - 제목: "테스트 게시글 제목"
   - 카테고리: "일반"
   - 내용: "이것은 테스트 게시글입니다. 다양한 기능을 테스트해보겠습니다."
4. "저장" 버튼 클릭

**예상 결과**:
- ✅ 모달이 닫히고 게시글 목록에 새 글이 표시됨
- ✅ 작성자, 작성일, 조회수, 좋아요 수가 정상 표시됨
- ✅ 통계 카드의 "전체 게시글" 수가 증가함

#### 1.2 게시글 상세 보기 테스트
**목표**: 게시글 상세 정보가 정상 표시되는지 확인

**단계**:
1. 게시글 목록에서 아무 게시글 클릭
2. 상세 정보 모달 확인

**예상 결과**:
- ✅ 게시글 제목, 내용, 작성자 정보 표시
- ✅ 조회수, 좋아요 수, 댓글 수 표시
- ✅ 좋아요 버튼 클릭 시 좋아요 수 증가

---

### 시나리오 2: 파일 업로드 기능 테스트

#### 2.1 파일 첨부 테스트
**목표**: 게시글 작성 시 파일 첨부 기능이 정상 작동하는지 확인

**단계**:
1. 새 글 작성 모달 열기
2. 파일 업로드 영역에 이미지 파일 드래그 앤 드롭
3. 파일 목록에서 업로드된 파일 확인
4. 게시글 저장

**예상 결과**:
- ✅ 파일이 목록에 표시됨
- ✅ 파일명과 크기가 정상 표시됨
- ✅ 삭제 버튼으로 파일 제거 가능

#### 2.2 파일 다운로드 테스트
**목표**: 첨부된 파일 다운로드가 정상 작동하는지 확인

**단계**:
1. 파일이 첨부된 게시글 작성
2. 게시글 상세 보기에서 파일 확인
3. 파일 다운로드 링크 클릭

**예상 결과**:
- ✅ 파일이 정상적으로 다운로드됨
- ✅ 원본 파일명으로 저장됨

---

### 시나리오 3: 고급 검색 및 정렬 기능 테스트

#### 3.1 키워드 검색 테스트
**목표**: 제목과 내용에서 키워드 검색이 정상 작동하는지 확인

**단계**:
1. 검색 폼에서 키워드 입력 (예: "테스트")
2. "🔍 검색" 버튼 클릭

**예상 결과**:
- ✅ 키워드가 포함된 게시글만 표시됨
- ✅ 검색 결과가 페이징으로 표시됨

#### 3.2 카테고리 필터 테스트
**목표**: 카테고리별 필터링이 정상 작동하는지 확인

**단계**:
1. 카테고리 드롭다운에서 "기술" 선택
2. 검색 실행

**예상 결과**:
- ✅ 선택한 카테고리의 게시글만 표시됨

#### 3.3 정렬 기능 테스트
**목표**: 다양한 정렬 옵션이 정상 작동하는지 확인

**단계**:
1. 정렬 옵션을 "최신순"으로 설정 후 검색
2. 정렬 옵션을 "인기순"으로 변경 후 검색
3. 정렬 옵션을 "좋아요순"으로 변경 후 검색

**예상 결과**:
- ✅ "최신순": 최근 작성된 게시글부터 표시
- ✅ "인기순": 조회수가 높은 게시글부터 표시
- ✅ "좋아요순": 좋아요가 많은 게시글부터 표시

#### 3.4 복합 검색 테스트
**목표**: 키워드와 카테고리를 동시에 적용한 검색이 정상 작동하는지 확인

**단계**:
1. 키워드 입력: "Spring"
2. 카테고리 선택: "기술"
3. 정렬: "최신순"
4. 검색 실행

**예상 결과**:
- ✅ "Spring" 키워드가 포함된 기술 카테고리 게시글만 표시
- ✅ 최신순으로 정렬됨

---

### 시나리오 4: 페이징 기능 테스트

#### 4.1 페이징 네비게이션 테스트
**목표**: 페이지 이동이 정상 작동하는지 확인

**단계**:
1. 여러 게시글 작성 (최소 15개 이상)
2. 페이지 하단의 페이징 버튼 확인
3. "다음" 버튼 클릭
4. "이전" 버튼 클릭

**예상 결과**:
- ✅ 페이지 번호가 정상 표시됨
- ✅ 페이지 이동 시 게시글 목록이 변경됨
- ✅ 현재 페이지가 강조 표시됨

---

### 시나리오 5: 통계 기능 테스트

#### 5.1 통계 카드 확인
**목표**: 상단 통계 카드가 정확한 정보를 표시하는지 확인

**단계**:
1. 게시글 작성
2. 게시글 조회
3. 좋아요 클릭
4. 통계 카드 확인

**예상 결과**:
- ✅ "전체 게시글" 수가 정확히 표시됨
- ✅ "총 조회수"가 정확히 표시됨
- ✅ "총 좋아요" 수가 정확히 표시됨
- ✅ "총 댓글" 수가 정확히 표시됨

---

### 시나리오 6: API 테스트

#### 6.1 고급 API 테스트 페이지
**목표**: `api-enhanced.html`에서 모든 API 기능이 정상 작동하는지 확인

**단계**:
1. `http://localhost:8080/api-enhanced.html` 접속
2. 각 섹션별로 테스트 실행:
   - 사용자 관리
   - 게시글 관리
   - 고급 검색
   - 파일 업로드
   - 게시글 조회
   - 좋아요 기능

**예상 결과**:
- ✅ 모든 API 호출이 성공적으로 실행됨
- ✅ 응답 결과가 JSON 형태로 정상 표시됨
- ✅ 오류 발생 시 적절한 오류 메시지 표시

#### 6.2 파일 업로드 API 테스트
**단계**:
1. 파일 업로드 섹션에서 파일 선택
2. 게시글 ID와 사용자 ID 입력
3. "파일 업로드" 버튼 클릭

**예상 결과**:
- ✅ 파일이 성공적으로 업로드됨
- ✅ 업로드된 파일 정보가 JSON으로 반환됨

---

### 시나리오 7: 데이터베이스 확인

#### 7.1 H2 콘솔 확인
**목표**: 데이터베이스에 데이터가 정상 저장되는지 확인

**단계**:
1. `http://localhost:8080/h2-console` 접속
2. 연결 정보 입력:
   - JDBC URL: `jdbc:h2:mem:testdb`
   - 사용자명: `sa`
   - 비밀번호: (빈 값)
3. 테이블 확인:
   - `users` 테이블
   - `posts` 테이블
   - `comments` 테이블
   - `file_uploads` 테이블

**예상 결과**:
- ✅ 모든 테이블이 정상 생성됨
- ✅ 테스트 데이터가 정상 저장됨
- ✅ 외래 키 관계가 정상 설정됨

---

## 🐛 문제 해결 가이드

### 일반적인 문제들

#### 1. 포트 8080 충돌
**증상**: 애플리케이션 시작 시 포트 충돌 오류
**해결**: `gradlew.bat`에 자동 종료 기능이 추가되어 있으므로 재실행

#### 2. 파일 업로드 실패
**증상**: 파일 업로드 시 오류 발생
**해결**: 
- 파일 크기 확인 (10MB 이하)
- 허용된 파일 타입 확인
- 업로드 디렉토리 권한 확인

#### 3. 검색 결과 없음
**증상**: 검색 시 결과가 표시되지 않음
**해결**:
- 데이터베이스에 게시글 데이터 확인
- 검색 키워드 확인
- 카테고리 설정 확인

#### 4. 페이징 오류
**증상**: 페이지 이동 시 오류 발생
**해결**:
- 페이지 번호 확인 (0부터 시작)
- 페이지 크기 확인 (기본값: 10)

---

## 📊 테스트 체크리스트

### 기본 기능
- [ ] 게시글 작성
- [ ] 게시글 조회
- [ ] 게시글 수정
- [ ] 게시글 삭제
- [ ] 댓글 작성
- [ ] 댓글 조회

### 강화 기능
- [ ] 고급 검색 (키워드)
- [ ] 카테고리 필터
- [ ] 정렬 기능 (최신순, 인기순, 좋아요순)
- [ ] 페이징 처리
- [ ] 조회수 증가
- [ ] 좋아요 기능
- [ ] 통계 표시

### 파일 업로드
- [ ] 파일 첨부
- [ ] 파일 다운로드
- [ ] 파일 미리보기
- [ ] 파일 삭제
- [ ] 드래그 앤 드롭

### API 테스트
- [ ] 사용자 API
- [ ] 게시글 API
- [ ] 댓글 API
- [ ] 파일 API
- [ ] 검색 API

---

## 🎯 성공 기준

모든 테스트 시나리오가 다음 기준을 만족해야 합니다:

1. **기능적 완성도**: 모든 기능이 정상 작동
2. **사용자 경험**: 직관적이고 반응성 좋은 인터페이스
3. **데이터 무결성**: 데이터가 정확히 저장되고 조회됨
4. **오류 처리**: 적절한 오류 메시지와 복구 기능
5. **성능**: 적절한 응답 시간과 페이징 처리

---

## 📝 테스트 결과 기록

테스트 완료 후 다음 정보를 기록하세요:

- **테스트 날짜**: 
- **테스트 환경**: 
- **성공한 기능**: 
- **발견된 문제**: 
- **개선 사항**: 
- **전체 평가**: 

---

## 🔍 현재 애플리케이션 상태

로그를 보면 애플리케이션이 정상적으로 실행되고 있습니다:

- ✅ Spring Boot 3.5.3 시작 완료
- ✅ H2 데이터베이스 연결 성공
- ✅ JPA 엔티티 생성 완료 (users, posts, comments, file_uploads)
- ✅ Tomcat 서버 8080 포트에서 실행 중
- ✅ 기본 관리자 계정 생성 완료 (admin/admin)
- ✅ API 요청 처리 중 (게시글 조회 쿼리 실행됨)

---

💡 **팁**: 테스트는 단계별로 진행하고, 각 단계에서 예상 결과와 실제 결과를 비교하여 문제를 조기에 발견하세요! 