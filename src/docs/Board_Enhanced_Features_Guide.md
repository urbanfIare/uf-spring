# 게시판 강화 기능 가이드

## 📋 개요

이 가이드는 Spring Boot 게시판에 추가된 고급 기능들을 설명합니다.

## 🚀 주요 기능

### 1. 고급 검색 및 정렬

#### 검색 기능
- **키워드 검색**: 제목과 내용에서 키워드 검색
- **카테고리 필터**: 특정 카테고리로 필터링
- **복합 검색**: 키워드 + 카테고리 동시 검색

#### 정렬 기능
- **최신순** (`latest`): 생성일 기준 내림차순
- **인기순** (`popular`): 조회수 기준 내림차순
- **좋아요순** (`mostLiked`): 좋아요 수 기준 내림차순
- **조회수순** (`mostViewed`): 조회수 기준 내림차순

#### API 사용법
```bash
# 고급 검색 API
POST /api/posts/search
Content-Type: application/json

{
  "keyword": "검색어",
  "category": "TECH",
  "sortBy": "latest",
  "sortOrder": "desc",
  "page": 0,
  "size": 10
}
```

### 2. 페이징 처리

#### 페이징 정보
- **page**: 현재 페이지 (0부터 시작)
- **size**: 페이지당 게시글 수
- **totalElements**: 전체 게시글 수
- **totalPages**: 전체 페이지 수

#### 응답 예시
```json
{
  "content": [
    {
      "id": 1,
      "title": "게시글 제목",
      "content": "게시글 내용",
      "authorName": "작성자",
      "category": "TECH",
      "viewCount": 10,
      "likeCount": 5,
      "commentCount": 3
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 25,
  "totalPages": 3
}
```

### 3. 조회수 및 좋아요 기능

#### 조회수 증가
```bash
POST /api/posts/{id}/view
```

#### 좋아요 기능
```bash
# 좋아요 증가
POST /api/posts/{id}/like

# 좋아요 감소
POST /api/posts/{id}/unlike
```

### 4. 통계 기능

#### 인기 게시글
```bash
GET /api/posts/popular
```

#### 좋아요 많은 게시글
```bash
GET /api/posts/most-liked
```

#### 최근 게시글 (7일)
```bash
GET /api/posts/recent
```

## 🔧 구현 세부사항

### PostService 강화

#### 검색 및 페이징 메서드
```java
public PostDto.PageResponse searchPostsWithPaging(PostDto.SearchRequest request) {
    // 정렬 설정
    Sort sort = createSort(request.getSortBy(), request.getSortOrder());
    
    // 페이징 설정
    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
    
    // 검색 조건에 따른 쿼리 실행
    Page<Post> postPage;
    
    if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
        if (request.getCategory() != null) {
            // 키워드 + 카테고리 검색
            postPage = postRepository.findByTitleContainingOrContentContainingAndCategoryAndIsDeletedFalse(
                    request.getKeyword(), request.getKeyword(), request.getCategory(), pageable);
        } else {
            // 키워드만 검색
            postPage = postRepository.findByTitleContainingOrContentContainingAndIsDeletedFalse(
                    request.getKeyword(), pageable);
        }
    } else if (request.getCategory() != null) {
        // 카테고리만 검색
        postPage = postRepository.findByCategoryAndIsDeletedFalse(request.getCategory(), pageable);
    } else {
        // 전체 조회
        postPage = postRepository.findByIsDeletedFalse(pageable);
    }
    
    // DTO 변환 및 반환
    return new PostDto.PageResponse(...);
}
```

### PostRepository 확장

#### 페이징을 위한 메서드들
```java
// 페이징을 위한 메서드들
Page<Post> findByIsDeletedFalse(Pageable pageable);

Page<Post> findByCategoryAndIsDeletedFalse(PostCategory category, Pageable pageable);

@Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND p.isDeleted = false")
Page<Post> findByTitleContainingOrContentContainingAndIsDeletedFalse(@Param("keyword") String keyword, Pageable pageable);

@Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND p.category = :category AND p.isDeleted = false")
Page<Post> findByTitleContainingOrContentContainingAndCategoryAndIsDeletedFalse(
        @Param("keyword") String keyword, @Param("category") PostCategory category, Pageable pageable);
```

## 🎨 웹 인터페이스

### board-enhanced.html

#### 주요 기능
- **고급 검색 폼**: 키워드, 카테고리, 정렬 옵션
- **통계 카드**: 전체 게시글, 조회수, 좋아요, 댓글 수
- **게시글 카드**: 호버 효과와 상세 정보 표시
- **페이징**: 페이지 네비게이션
- **모달**: 게시글 작성/상세 보기

#### CSS 특징
- **그라데이션 배경**: 모던한 디자인
- **카드 레이아웃**: 깔끔한 게시글 표시
- **호버 효과**: 사용자 경험 향상
- **반응형 디자인**: 모바일 친화적

## 📊 성능 최적화

### 1. 데이터베이스 인덱싱
```sql
-- 조회수 인덱스
CREATE INDEX idx_posts_view_count ON posts(view_count DESC);

-- 좋아요 수 인덱스
CREATE INDEX idx_posts_like_count ON posts(like_count DESC);

-- 생성일 인덱스
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
```

### 2. 페이징 최적화
- **LIMIT/OFFSET** 대신 **커서 기반 페이징** 고려
- **인덱스 힌트** 사용으로 쿼리 최적화

### 3. 캐싱 전략
```java
@Cacheable("posts")
public List<PostDto.Response> getPopularPosts() {
    // 인기 게시글 조회 로직
}
```

## 🔍 테스트 방법

### 1. 웹 인터페이스 테스트
```bash
# 브라우저에서 접속
http://localhost:8080/board-enhanced.html
```

### 2. API 테스트
```bash
# 고급 검색 테스트
curl -X POST http://localhost:8080/api/posts/search \
  -H "Content-Type: application/json" \
  -d '{
    "keyword": "Spring",
    "category": "TECH",
    "sortBy": "latest",
    "page": 0,
    "size": 10
  }'
```

### 3. 통계 확인
```bash
# 인기 게시글 조회
curl http://localhost:8080/api/posts/popular

# 좋아요 많은 게시글 조회
curl http://localhost:8080/api/posts/most-liked
```

## 🚀 향후 개선 사항

### 1. 추가 기능
- **실시간 알림**: 새 댓글, 좋아요 알림
- **북마크 기능**: 관심 게시글 저장
- **태그 시스템**: 게시글 태그 기능
- **추천 시스템**: 사용자 맞춤 게시글 추천

### 2. 성능 개선
- **Redis 캐싱**: 자주 조회되는 데이터 캐싱
- **Elasticsearch**: 전문 검색 엔진 도입
- **CDN**: 이미지 및 정적 파일 CDN 활용

### 3. 모니터링
- **로깅**: 사용자 행동 분석
- **메트릭**: 성능 지표 수집
- **알림**: 오류 및 성능 이슈 알림

---

💡 **팁**: 이 가이드의 기능들을 단계적으로 구현하여 사용자 경험을 향상시킬 수 있습니다! 