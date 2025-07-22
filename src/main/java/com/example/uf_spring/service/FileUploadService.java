package com.example.uf_spring.service;

import com.example.uf_spring.dto.FileUploadDto;
import com.example.uf_spring.model.FileUpload;
import com.example.uf_spring.model.Post;
import com.example.uf_spring.model.User;
import com.example.uf_spring.repository.FileUploadRepository;
import com.example.uf_spring.repository.PostRepository;
import com.example.uf_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadRepository fileUploadRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    // 파일 업로드
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
    
    // 파일 삭제
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
    
    // 파일 다운로드
    public File getFileForDownload(Long fileId) {
        FileUpload file = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        
        return new File(file.getFilePath());
    }
    
    // DTO 변환
    private FileUploadDto.Response convertToDto(FileUpload file) {
        return new FileUploadDto.Response(
                file.getId(),
                file.getOriginalFileName(),
                file.getStoredFileName(),
                file.getFilePath(),
                file.getFileType(),
                file.getFileSize(),
                file.getUploadedAt(),
                file.getPost() != null ? file.getPost().getId() : null,
                file.getUser() != null ? file.getUser().getId() : null
        );
    }
} 