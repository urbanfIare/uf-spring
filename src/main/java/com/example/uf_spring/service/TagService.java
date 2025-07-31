package com.example.uf_spring.service;

import com.example.uf_spring.dto.TagDto;
import com.example.uf_spring.model.Tag;
import com.example.uf_spring.model.Post;
import com.example.uf_spring.repository.TagRepository;
import com.example.uf_spring.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    // 태그 생성 (CreateRequest 사용)
    public TagDto.Response createTag(TagDto.CreateRequest request) {
        return addTag(request.getName());
    }

    // 태그 추가 (중복시 기존 태그 반환)
    public TagDto.Response addTag(String name) {
        Optional<Tag> existing = tagRepository.findByName(name);
        Tag tag = existing.orElseGet(() -> tagRepository.save(new Tag(name)));
        return toDto(tag);
    }

    // 모든 태그 조회
    public List<TagDto.Response> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream().map(this::toDto).collect(Collectors.toList());
    }

    // 태그 삭제
    public boolean deleteTag(Long tagId) {
        Optional<Tag> tag = tagRepository.findById(tagId);
        if (tag.isPresent()) {
            tagRepository.delete(tag.get());
            return true;
        }
        return false;
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