package com.example.uf_spring.controller;

import com.example.uf_spring.dto.FileUploadDto;
import com.example.uf_spring.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    // 파일 업로드
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

    // 게시글의 파일들 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<FileUploadDto.Response>> getFilesByPostId(@PathVariable Long postId) {
        try {
            List<FileUploadDto.Response> files = fileUploadService.getFilesByPostId(postId);
            return ResponseEntity.ok(files);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 사용자의 파일들 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FileUploadDto.Response>> getFilesByUserId(@PathVariable Long userId) {
        try {
            List<FileUploadDto.Response> files = fileUploadService.getFilesByUserId(userId);
            return ResponseEntity.ok(files);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 파일 다운로드
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

    // 파일 삭제
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long fileId,
            @RequestParam Long userId) {
        try {
            boolean deleted = fileUploadService.deleteFile(fileId, userId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 파일 미리보기 (이미지인 경우)
    @GetMapping("/preview/{fileId}")
    public ResponseEntity<Resource> previewFile(@PathVariable Long fileId) {
        try {
            File file = fileUploadService.getFileForDownload(fileId);
            Path path = Paths.get(file.getAbsolutePath());
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // 기본값, 실제로는 파일 타입에 따라 결정
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
} 