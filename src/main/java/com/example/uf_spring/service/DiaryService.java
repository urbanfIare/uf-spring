package com.example.uf_spring.service;

import com.example.uf_spring.dto.DiaryDto;
import com.example.uf_spring.model.Diary;
import com.example.uf_spring.model.Diary.Weather;
import com.example.uf_spring.model.Diary.Mood;
import com.example.uf_spring.model.DiaryImage;
import com.example.uf_spring.model.Tag;
import com.example.uf_spring.model.User;
import com.example.uf_spring.repository.DiaryRepository;
import com.example.uf_spring.repository.DiaryImageRepository;
import com.example.uf_spring.repository.TagRepository;
import com.example.uf_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Transactional
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryImageRepository diaryImageRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    // 일기 생성
    public DiaryDto.Response createDiary(DiaryDto.CreateRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Diary diary = new Diary(
                request.getTitle(),
                request.getContent(),
                author,
                request.getWeather(),
                request.getMood()
        );
        
        diary.setPrivate(request.getIsPrivate());
        
        // 태그 처리
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            List<Tag> tags = request.getTags().stream()
                    .map(tagName -> {
                        Optional<Tag> existingTag = tagRepository.findByName(tagName);
                        if (existingTag.isPresent()) {
                            return existingTag.get();
                        } else {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        }
                    })
                    .collect(Collectors.toList());
            diary.setTags(tags);
        }
        
        Diary savedDiary = diaryRepository.save(diary);
        return convertToResponse(savedDiary);
    }

    // 모든 일기 조회 (작성자별)
    public List<DiaryDto.Response> getAllDiariesByAuthor(Long authorId) {
        List<Diary> diaries = diaryRepository.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(authorId);
        return diaries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 일기 상세 조회
    public DiaryDto.Response getDiaryById(Long id, Long authorId) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));
        
        if (diary.isDeleted()) {
            throw new RuntimeException("삭제된 일기입니다.");
        }
        
        // 비공개 일기는 작성자만 조회 가능
        if (diary.isPrivate() && !diary.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("비공개 일기입니다.");
        }
        
        return convertToResponse(diary);
    }

    // 일기 수정
    public DiaryDto.Response updateDiary(Long id, DiaryDto.UpdateRequest request, Long authorId) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));
        
        if (diary.isDeleted()) {
            throw new RuntimeException("삭제된 일기입니다.");
        }
        
        if (!diary.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("일기를 수정할 권한이 없습니다.");
        }
        
        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());
        diary.setWeather(request.getWeather());
        diary.setMood(request.getMood());
        diary.setPrivate(request.getIsPrivate());
        diary.updateModifiedTime();
        
        // 태그 처리
        if (request.getTags() != null) {
            List<Tag> tags = request.getTags().stream()
                    .map(tagName -> {
                        Optional<Tag> existingTag = tagRepository.findByName(tagName);
                        if (existingTag.isPresent()) {
                            return existingTag.get();
                        } else {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        }
                    })
                    .collect(Collectors.toList());
            diary.setTags(tags);
        }
        
        Diary updatedDiary = diaryRepository.save(diary);
        return convertToResponse(updatedDiary);
    }

    // 일기 삭제 (소프트 삭제)
    public boolean deleteDiary(Long id, Long authorId) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));
        
        if (!diary.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("일기를 삭제할 권한이 없습니다.");
        }
        
        diary.setDeleted(true);
        diaryRepository.save(diary);
        
        // 관련 이미지들도 삭제 처리
        List<DiaryImage> images = diaryImageRepository.findByDiaryId(id);
        images.forEach(image -> image.setDeleted(true));
        diaryImageRepository.saveAll(images);
        
        return true;
    }

    // 날씨별 일기 조회
    public List<DiaryDto.Response> getDiariesByWeather(Weather weather, Long authorId) {
        List<Diary> diaries = diaryRepository.findByWeatherAndIsDeletedFalseOrderByCreatedAtDesc(weather);
        return diaries.stream()
                .filter(diary -> !diary.isPrivate() || diary.getAuthor().getId().equals(authorId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 기분별 일기 조회
    public List<DiaryDto.Response> getDiariesByMood(Mood mood, Long authorId) {
        List<Diary> diaries = diaryRepository.findByMoodAndIsDeletedFalseOrderByCreatedAtDesc(mood);
        return diaries.stream()
                .filter(diary -> !diary.isPrivate() || diary.getAuthor().getId().equals(authorId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 날짜별 일기 조회
    public List<DiaryDto.Response> getDiariesByDate(LocalDateTime date, Long authorId) {
        List<Diary> diaries = diaryRepository.findByCreatedDate(date);
        return diaries.stream()
                .filter(diary -> !diary.isPrivate() || diary.getAuthor().getId().equals(authorId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 월별 일기 조회
    public List<DiaryDto.Response> getDiariesByMonth(int year, int month, Long authorId) {
        List<Diary> diaries = diaryRepository.findByYearAndMonth(year, month);
        return diaries.stream()
                .filter(diary -> !diary.isPrivate() || diary.getAuthor().getId().equals(authorId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 최근 일기 조회 (최근 7일)
    public List<DiaryDto.Response> getRecentDiaries(Long authorId) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<Diary> diaries = diaryRepository.findRecentDiaries(startDate);
        return diaries.stream()
                .filter(diary -> !diary.isPrivate() || diary.getAuthor().getId().equals(authorId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 일기 검색
    public List<DiaryDto.Response> searchDiaries(String keyword, Long authorId) {
        List<Diary> diaries = diaryRepository.findByTitleOrContentContaining(keyword);
        return diaries.stream()
                .filter(diary -> !diary.isPrivate() || diary.getAuthor().getId().equals(authorId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 페이징 검색
    public DiaryDto.PageResponse searchDiariesWithPaging(DiaryDto.SearchRequest request, Long authorId) {
        Pageable pageable = createPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortOrder());
        Page<Diary> diaryPage = diaryRepository.findByIsDeletedFalse(pageable);
        
        List<DiaryDto.Response> responses = diaryPage.getContent().stream()
                .filter(diary -> !diary.isPrivate() || diary.getAuthor().getId().equals(authorId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new DiaryDto.PageResponse(
                responses,
                request.getPage(),
                request.getSize(),
                diaryPage.getTotalElements(),
                diaryPage.getTotalPages()
        );
    }

    // 통계 조회
    public DiaryDto.StatisticsResponse getDiaryStatistics(Long authorId) {
        Long totalDiaries = diaryRepository.countByAuthorId(authorId);
        
        // 날씨별 통계 (가장 많은 날씨)
        Weather mostWeather = null;
        Long maxWeatherCount = 0L;
        for (Weather weather : Weather.values()) {
            Long count = diaryRepository.countByAuthorIdAndWeather(authorId, weather);
            if (count > maxWeatherCount) {
                maxWeatherCount = count;
                mostWeather = weather;
            }
        }
        
        // 기분별 통계 (가장 많은 기분)
        Mood mostMood = null;
        Long maxMoodCount = 0L;
        for (Mood mood : Mood.values()) {
            Long count = diaryRepository.countByAuthorIdAndMood(authorId, mood);
            if (count > maxMoodCount) {
                maxMoodCount = count;
                mostMood = mood;
            }
        }
        
        return new DiaryDto.StatisticsResponse(
                totalDiaries,
                maxWeatherCount,
                maxMoodCount,
                List.of(Weather.values()),
                List.of(Mood.values())
        );
    }

    // 이미지 업로드
    public DiaryImage uploadImage(String originalFileName, String storedFileName, String filePath, 
                                Long fileSize, String contentType, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));
        
        DiaryImage image = new DiaryImage(originalFileName, storedFileName, filePath, fileSize, contentType, diary);
        return diaryImageRepository.save(image);
    }

    // 이미지 조회
    public List<String> getDiaryImageUrls(Long diaryId) {
        List<DiaryImage> images = diaryImageRepository.findByDiaryIdAndIsDeletedFalseOrderByUploadedAtAsc(diaryId);
        return images.stream()
                .map(DiaryImage::getFilePath)
                .collect(Collectors.toList());
    }

    // 이미지 삭제
    public boolean deleteImage(Long imageId, Long authorId) {
        DiaryImage image = diaryImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다."));
        
        if (!image.getDiary().getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("이미지를 삭제할 권한이 없습니다.");
        }
        
        image.setDeleted(true);
        diaryImageRepository.save(image);
        return true;
    }

    // DTO 변환
    private DiaryDto.Response convertToResponse(Diary diary) {
        List<String> imageUrls = getDiaryImageUrls(diary.getId());
        List<String> tags = diary.getTags() != null ? 
                diary.getTags().stream().map(Tag::getName).collect(Collectors.toList()) : 
                List.of();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        return new DiaryDto.Response(
                diary.getId(),
                diary.getTitle(),
                diary.getContent(),
                diary.getAuthor().getName(),
                diary.getAuthor().getId(),
                diary.getWeather(),
                diary.getMood(),
                diary.isPrivate(),
                diary.getCreatedAt().format(formatter),
                diary.getUpdatedAt().format(formatter),
                imageUrls,
                tags
        );
    }

    // 페이징 설정
    private Pageable createPageable(int page, int size, String sortBy, String sortOrder) {
        Sort sort = createSort(sortBy, sortOrder);
        return PageRequest.of(page, size, sort);
    }

    // 정렬 설정
    private Sort createSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        switch (sortBy) {
            case "oldest":
                return Sort.by(direction, "createdAt");
            case "mood":
                return Sort.by(direction, "mood");
            case "weather":
                return Sort.by(direction, "weather");
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }
} 