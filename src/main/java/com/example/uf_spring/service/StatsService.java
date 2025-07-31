package com.example.uf_spring.service;

import com.example.uf_spring.model.Post;
import com.example.uf_spring.model.PostCategory;
import com.example.uf_spring.model.Tag;
import com.example.uf_spring.repository.PostRepository;
import com.example.uf_spring.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public Map<String, Object> getSummary() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Post> allPosts = postRepository.findByIsDeletedFalseOrderByCreatedAtDesc();

        // 전체 게시글 수
        result.put("totalPosts", allPosts.size());
        // 카테고리별 게시글 수
        Map<String, Long> categoryCount = Arrays.stream(PostCategory.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        cat -> allPosts.stream().filter(p -> p.getCategory() == cat).count()
                ));
        result.put("categoryCount", categoryCount);
        // 일별 게시글 수(최근 14일)
        Map<String, Long> dailyCount = allPosts.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCreatedAt().toLocalDate().toString(),
                        TreeMap::new,
                        Collectors.counting()
                ));
        result.put("dailyCount", dailyCount);
        // 인기 게시글(조회수 상위 5개)
        List<Map<String, Object>> popularPosts = allPosts.stream()
                .sorted(Comparator.comparingInt(Post::getViewCount).reversed())
                .limit(5)
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", p.getId());
                    m.put("title", p.getTitle());
                    m.put("viewCount", p.getViewCount());
                    return m;
                }).collect(Collectors.toList());
        result.put("popularPosts", popularPosts);
        // 인기 태그(상위 5개)
        List<Tag> allTags = tagRepository.findAll();
        List<Map<String, Object>> popularTags = allTags.stream()
                .sorted(Comparator.comparingInt(t -> -t.getPosts().size()))
                .limit(5)
                .map(t -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", t.getName());
                    m.put("count", t.getPosts().size());
                    return m;
                }).collect(Collectors.toList());
        result.put("popularTags", popularTags);
        return result;
    }
} 