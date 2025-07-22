package com.example.uf_spring.service;

import com.example.uf_spring.dto.TagDto;
import com.example.uf_spring.model.Tag;
import com.example.uf_spring.model.Post;
import com.example.uf_spring.repository.TagRepository;
import com.example.uf_spring.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostRepository postRepository;

    // 태그 추가 (중복시 기존 태그 반환)
    public TagDto.Response addTag(String name) {
        Optional<Tag> existing = tagRepository.findByName(name);
        Tag tag = existing.orElseGet(() -> tagRepository.save(new Tag(name)));
        return toDto(tag);
    }

    // 자동완성
    public List<TagDto.Response> autocomplete(String keyword) {
        List<Tag> tags = tagRepository.findByNameContainingIgnoreCase(keyword);
        return tags.stream().map(this::toDto).collect(Collectors.toList());
    }

    // 태그별 게시글 조회
    public List<Long> getPostIdsByTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName)
                .orElseThrow(() -> new RuntimeException("태그를 찾을 수 없습니다."));
        return tag.getPosts().stream().map(Post::getId).collect(Collectors.toList());
    }

    private TagDto.Response toDto(Tag tag) {
        return new TagDto.Response(tag.getId(), tag.getName());
    }
} 