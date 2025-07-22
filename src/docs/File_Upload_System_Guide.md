# 파일 업로드 시스템 가이드

## 📋 개요

이 가이드는 Spring Boot에서 구현된 파일 업로드 시스템을 설명합니다.

## 🚀 주요 기능

### 1. 파일 업로드
- **다중 파일 업로드**: 여러 파일 동시 업로드
- **드래그 앤 드롭**: 직관적인 파일 업로드
- **파일 크기 제한**: 10MB까지 업로드 가능
- **파일 타입 검증**: 안전한 파일 타입만 허용

### 2. 파일 관리
- **파일 조회**: 게시글별, 사용자별 파일 조회
- **파일 다운로드**: 원본 파일명으로 다운로드
- **파일 미리보기**: 이미지 파일 미리보기
- **파일 삭제**: 권한 확인 후 파일 삭제

### 3. 보안 기능
- **파일명 중복 방지**: UUID 기반 고유 파일명 생성
- **권한 확인**: 파일 소유자만 삭제 가능
- **파일 경로 보안**: 상대 경로 사용으로 보안 강화

## 🔧 구현 세부사항

### 1. FileUpload 엔티티

```java
@Entity
@Table(name = "file_uploads")
public class FileUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String originalFileName;  // 원본 파일명
    
    @Column(nullable = false)
    private String storedFileName;    // 저장된 파일명 (UUID)
    
    @Column(nullable = false)
    private String filePath;          // 파일 경로
    
    @Column(nullable = false)
    private String fileType;          // 파일 타입 (MIME)
    
    @Column(nullable = false)
    private Long fileSize;            // 파일 크기
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;                // 연결된 게시글
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                // 업로드한 사용자
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt; // 업로드 시간
    
    @Column(nullable = false)
    private boolean isDeleted = false; // 삭제 여부
}
```

### 2. FileUploadService

#### 파일 업로드 메서드
```java
public FileUploadDto.Response uploadFile(MultipartFile file, Long postId, Long userId) throws IOException {
    // 사용자와 게시글 확인
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    
    // 업로드 디렉토리 생성
    Path uploadDir = Paths.get(uploadPath);
    if (!Files.exists(uploadDir)) {
        Files.createDirectories(uploadDir);
    }
    
    // 파일명 생성 (중복 방지)
    String originalFileName = file.getOriginalFilename();
    String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
    String storedFileName = UUID.randomUUID().toString() + fileExtension;
    
    // 파일 저장
    Path filePath = uploadDir.resolve(storedFileName);
    Files.copy(file.getInputStream(), filePath);
    
    // 데이터베이스에 저장
    FileUpload fileUpload = new FileUpload(
            originalFileName,
            storedFileName,
            filePath.toString(),
            file.getContentType(),
            file.getSize(),
            post,
            user
    );
    
    FileUpload savedFile = fileUploadRepository.save(fileUpload);
    
    return convertToDto(savedFile);
}
```

#### 파일 조회 메서드
```java
// 게시글의 파일들 조회
public List<FileUploadDto.Response> getFilesByPostId(Long postId) {
    List<FileUpload> files = fileUploadRepository.findByPostId(postId);
    return files.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
}

// 사용자의 파일들 조회
public List<FileUploadDto.Response> getFilesByUserId(Long userId) {
    List<FileUpload> files = fileUploadRepository.findByUserId(userId);
    return files.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
}
```

#### 파일 삭제 메서드
```java
public boolean deleteFile(Long fileId, Long userId) {
    FileUpload file = fileUploadRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
    
    // 권한 확인
    if (!file.getUser().getId().equals(userId)) {
        throw new RuntimeException("파일을 삭제할 권한이 없습니다.");
    }
    
    // 실제 파일 삭제
    try {
        Path filePath = Paths.get(file.getFilePath());
        Files.deleteIfExists(filePath);
    } catch (IOException e) {
        // 파일이 이미 삭제되었을 수 있음
    }
    
    // 데이터베이스에서 삭제
    file.setDeleted(true);
    fileUploadRepository.save(file);
    
    return true;
}
```

### 3. FileUploadController

#### 파일 업로드 API
```java
@PostMapping("/upload")
public ResponseEntity<FileUploadDto.Response> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("postId") Long postId,
        @RequestParam("userId") Long userId) {
    try {
        FileUploadDto.Response response = fileUploadService.uploadFile(file, postId, userId);
        return ResponseEntity.ok(response);
    } catch (IOException e) {
        return ResponseEntity.badRequest().build();
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }
}
```

#### 파일 다운로드 API
```java
@GetMapping("/download/{fileId}")
public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
    try {
        File file = fileUploadService.getFileForDownload(fileId);
        Path path = Paths.get(file.getAbsolutePath());
        Resource resource = new UrlResource(path.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (MalformedURLException e) {
        return ResponseEntity.badRequest().build();
    } catch (RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}
```

## ⚙️ 설정

### 1. application.properties

```properties
# 파일 업로드 설정
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.enabled=true

# 파일 업로드 경로 설정
file.upload.path=uploads
```

### 2. 파일 업로드 경로 설정

```java
@Value("${file.upload.path:uploads}")
private String uploadPath;
```

## 🎨 웹 인터페이스

### 드래그 앤 드롭 구현

```javascript
// 파일 업로드 설정
function setupFileUpload() {
    const fileUpload = document.getElementById('fileUpload');
    const fileInput = document.getElementById('fileInput');
    
    fileUpload.addEventListener('click', () => fileInput.click());
    fileUpload.addEventListener('dragover', (e) => {
        e.preventDefault();
        fileUpload.classList.add('dragover');
    });
    fileUpload.addEventListener('dragleave', () => {
        fileUpload.classList.remove('dragover');
    });
    fileUpload.addEventListener('drop', (e) => {
        e.preventDefault();
        fileUpload.classList.remove('dragover');
        const files = e.dataTransfer.files;
        handleFiles(files);
    });
    
    fileInput.addEventListener('change', (e) => {
        handleFiles(e.target.files);
    });
}

// 파일 처리
function handleFiles(files) {
    uploadedFiles = Array.from(files);
    displayFiles();
}

// 파일 목록 표시
function displayFiles() {
    const fileList = document.getElementById('fileList');
    fileList.innerHTML = uploadedFiles.map((file, index) => `
        <div class="file-item">
            <span>📎 ${file.name} (${formatFileSize(file.size)})</span>
            <button type="button" onclick="removeFile(${index})" 
                    style="background: #dc3545; color: white; border: none; 
                           padding: 4px 8px; border-radius: 3px; cursor: pointer;">삭제</button>
        </div>
    `).join('');
}
```

### CSS 스타일

```css
.file-upload {
    border: 2px dashed #e9ecef;
    border-radius: 8px;
    padding: 20px;
    text-align: center;
    margin: 10px 0;
    transition: all 0.3s;
}

.file-upload.dragover {
    border-color: #667eea;
    background: #f8f9ff;
}

.file-list {
    margin-top: 10px;
}

.file-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px;
    background: #f8f9fa;
    border-radius: 5px;
    margin-bottom: 5px;
}
```

## 🔍 API 엔드포인트

### 1. 파일 업로드
```bash
POST /api/files/upload
Content-Type: multipart/form-data

Parameters:
- file: 업로드할 파일
- postId: 게시글 ID
- userId: 사용자 ID
```

### 2. 게시글의 파일 조회
```bash
GET /api/files/post/{postId}
```

### 3. 사용자의 파일 조회
```bash
GET /api/files/user/{userId}
```

### 4. 파일 다운로드
```bash
GET /api/files/download/{fileId}
```

### 5. 파일 미리보기
```bash
GET /api/files/preview/{fileId}
```

### 6. 파일 삭제
```bash
DELETE /api/files/{fileId}?userId={userId}
```

## 🛡️ 보안 고려사항

### 1. 파일 타입 검증
```java
// 허용된 파일 타입 정의
private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
    "image/jpeg", "image/png", "image/gif", "image/webp",
    "application/pdf", "text/plain", "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
);

// 파일 타입 검증
private void validateFileType(MultipartFile file) {
    if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
        throw new RuntimeException("허용되지 않는 파일 타입입니다.");
    }
}
```

### 2. 파일 크기 제한
```properties
# 최대 파일 크기 제한
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### 3. 파일 경로 보안
```java
// 상대 경로 사용으로 보안 강화
@Value("${file.upload.path:uploads}")
private String uploadPath;

// 절대 경로 사용 시 주의사항
Path uploadDir = Paths.get(uploadPath);
if (!uploadDir.isAbsolute()) {
    uploadDir = Paths.get(System.getProperty("user.dir"), uploadPath);
}
```

## 📊 성능 최적화

### 1. 파일 업로드 최적화
```java
// 스트림 사용으로 메모리 효율성 향상
Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

// 비동기 업로드 고려
@Async
public CompletableFuture<FileUploadDto.Response> uploadFileAsync(MultipartFile file, Long postId, Long userId) {
    // 비동기 파일 업로드 로직
}
```

### 2. 파일 압축
```java
// 이미지 파일 압축
public void compressImage(Path imagePath) {
    try {
        BufferedImage originalImage = ImageIO.read(imagePath.toFile());
        BufferedImage compressedImage = new BufferedImage(
            originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = compressedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();
        
        ImageIO.write(compressedImage, "JPEG", imagePath.toFile());
    } catch (IOException e) {
        throw new RuntimeException("이미지 압축에 실패했습니다.", e);
    }
}
```

### 3. CDN 연동
```java
// CDN URL 생성
public String getCdnUrl(String fileName) {
    return "https://cdn.example.com/files/" + fileName;
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
# 파일 업로드 테스트
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@test.jpg" \
  -F "postId=1" \
  -F "userId=1"

# 파일 조회 테스트
curl http://localhost:8080/api/files/post/1

# 파일 다운로드 테스트
curl -O http://localhost:8080/api/files/download/1
```

### 3. 파일 업로드 테스트 페이지
```bash
# 브라우저에서 접속
http://localhost:8080/api-enhanced.html
```

## 🚀 향후 개선 사항

### 1. 추가 기능
- **이미지 썸네일 생성**: 업로드 시 자동 썸네일 생성
- **파일 미리보기**: 이미지, PDF 등 미리보기 기능
- **파일 버전 관리**: 파일 수정 시 버전 관리
- **클라우드 스토리지**: AWS S3, Google Cloud Storage 연동

### 2. 성능 개선
- **비동기 업로드**: 대용량 파일 비동기 처리
- **청크 업로드**: 대용량 파일 청크 단위 업로드
- **압축 전송**: 파일 압축 후 전송
- **캐싱**: 자주 다운로드되는 파일 캐싱

### 3. 보안 강화
- **바이러스 스캔**: 업로드 파일 바이러스 검사
- **파일 암호화**: 민감한 파일 암호화 저장
- **접근 로그**: 파일 접근 로그 기록
- **백업 시스템**: 파일 백업 및 복구

---

💡 **팁**: 파일 업로드 시스템은 보안과 성능을 고려하여 단계적으로 구현하는 것이 좋습니다! 