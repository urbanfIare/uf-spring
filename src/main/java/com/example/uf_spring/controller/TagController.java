package com.example.uf_spring.controller;

import com.example.uf_spring.dto.TagDto;
import com.example.uf_spring.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    // 태그 추가
    @PostMapping
    public ResponseEntity<TagDto.Response> addTag(@RequestParam String name) {
        try {
            TagDto.Response response = tagService.addTag(name);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<List<TagDto.Response>> autocomplete(@RequestParam String keyword) {
        try {
            List<TagDto.Response> list = tagService.autocomplete(keyword);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 태그별 게시글 ID 목록
    @GetMapping("/{tagName}/posts")
    public ResponseEntity<List<Long>> getPostIdsByTag(@PathVariable String tagName) {
        try {
            List<Long> ids = tagService.getPostIdsByTag(tagName);
            return ResponseEntity.ok(ids);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 