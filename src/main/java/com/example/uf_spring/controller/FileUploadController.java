package com.example.uf_spring.controller;

import com.example.uf_spring.dto.FileUploadDto;
import com.example.uf_spring.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "파일 업로드 관리", description = "파일 업로드, 다운로드, 조회, 삭제 API")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    // 파일 업로드
    @PostMapping("/upload")
    @Operation(summary = "파일 업로드", description = "게시글에 첨부할 파일을 업로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 또는 요청")
    })
    public ResponseEntity<FileUploadDto.Response> uploadFile(
            @Parameter(description = "업로드할 파일", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "게시글 ID", required = true) @RequestParam("postId") Long postId,
            @Parameter(description = "사용자 ID", required = true) @RequestParam("userId") Long userId) {
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
    @Operation(summary = "게시글 파일 조회", description = "특정 게시글에 첨부된 모든 파일을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<List<FileUploadDto.Response>> getFilesByPostId(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId) {
        try {
            List<FileUploadDto.Response> files = fileUploadService.getFilesByPostId(postId);
            return ResponseEntity.ok(files);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 사용자의 파일들 조회
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자 파일 조회", description = "특정 사용자가 업로드한 모든 파일을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<List<FileUploadDto.Response>> getFilesByUserId(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId) {
        try {
            List<FileUploadDto.Response> files = fileUploadService.getFilesByUserId(userId);
            return ResponseEntity.ok(files);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 파일 다운로드
    @GetMapping("/download/{fileId}")
    @Operation(summary = "파일 다운로드", description = "특정 파일을 다운로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 다운로드 성공"),
        @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
    })
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "파일 ID", required = true) @PathVariable Long fileId) {
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
    @Operation(summary = "파일 삭제", description = "특정 파일을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "삭제할 파일을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "파일 ID", required = true) @PathVariable Long fileId,
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId) {
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

    // 파일 미리보기
    @GetMapping("/preview/{fileId}")
    @Operation(summary = "파일 미리보기", description = "특정 파일을 미리보기합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 미리보기 성공"),
        @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
    })
    public ResponseEntity<Resource> previewFile(
            @Parameter(description = "파일 ID", required = true) @PathVariable Long fileId) {
        try {
            File file = fileUploadService.getFileForDownload(fileId);
            Path path = Paths.get(file.getAbsolutePath());
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
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