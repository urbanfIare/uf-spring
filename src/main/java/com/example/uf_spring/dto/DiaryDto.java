package com.example.uf_spring.dto;

import com.example.uf_spring.model.Diary;
import com.example.uf_spring.model.Diary.Weather;
import com.example.uf_spring.model.Diary.Mood;
import java.time.LocalDateTime;
import java.util.List;

public class DiaryDto {
    
    // 요청용 DTO
    public static class CreateRequest {
        private String title;
        private String content;
        private Weather weather;
        private Mood mood;
        private boolean isPrivate;
        private List<String> tags;
        
        public CreateRequest() {}
        
        public CreateRequest(String title, String content, Weather weather, Mood mood, boolean isPrivate, List<String> tags) {
            this.title = title;
            this.content = content;
            this.weather = weather;
            this.mood = mood;
            this.isPrivate = isPrivate;
            this.tags = tags;
        }
        
        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Weather getWeather() { return weather; }
        public void setWeather(Weather weather) { this.weather = weather; }
        public Mood getMood() { return mood; }
        public void setMood(Mood mood) { this.mood = mood; }
        public boolean getIsPrivate() { return isPrivate; }
        public void setIsPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }
    
    public static class UpdateRequest {
        private String title;
        private String content;
        private Weather weather;
        private Mood mood;
        private boolean isPrivate;
        private List<String> tags;
        
        public UpdateRequest() {}
        
        public UpdateRequest(String title, String content, Weather weather, Mood mood, boolean isPrivate, List<String> tags) {
            this.title = title;
            this.content = content;
            this.weather = weather;
            this.mood = mood;
            this.isPrivate = isPrivate;
            this.tags = tags;
        }
        
        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Weather getWeather() { return weather; }
        public void setWeather(Weather weather) { this.weather = weather; }
        public Mood getMood() { return mood; }
        public void setMood(Mood mood) { this.mood = mood; }
        public boolean getIsPrivate() { return isPrivate; }
        public void setIsPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }
    
    // 응답용 DTO
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String authorName;
        private Long authorId;
        private Weather weather;
        private Mood mood;
        private boolean isPrivate;
        private String createdAt;
        private String updatedAt;
        private List<String> imageUrls;
        private List<String> tags;
        
        public Response() {}
        
        public Response(Long id, String title, String content, String authorName, Long authorId, 
                      Weather weather, Mood mood, boolean isPrivate, String createdAt, 
                      String updatedAt, List<String> imageUrls, List<String> tags) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.authorName = authorName;
            this.authorId = authorId;
            this.weather = weather;
            this.mood = mood;
            this.isPrivate = isPrivate;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.imageUrls = imageUrls;
            this.tags = tags;
        }
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getAuthorName() { return authorName; }
        public void setAuthorName(String authorName) { this.authorName = authorName; }
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
        public Weather getWeather() { return weather; }
        public void setWeather(Weather weather) { this.weather = weather; }
        public Mood getMood() { return mood; }
        public void setMood(Mood mood) { this.mood = mood; }
        public boolean getIsPrivate() { return isPrivate; }
        public void setIsPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public List<String> getImageUrls() { return imageUrls; }
        public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }
    
    // 검색용 DTO
    public static class SearchRequest {
        private String keyword;
        private Weather weather;
        private Mood mood;
        private boolean isPrivate;
        private String sortBy; // "latest", "oldest", "mood", "weather"
        private String sortOrder; // "asc", "desc"
        private int page = 0;
        private int size = 10;
        
        public SearchRequest() {}
        
        public SearchRequest(String keyword, Weather weather, Mood mood, boolean isPrivate, 
                           String sortBy, String sortOrder, int page, int size) {
            this.keyword = keyword;
            this.weather = weather;
            this.mood = mood;
            this.isPrivate = isPrivate;
            this.sortBy = sortBy;
            this.sortOrder = sortOrder;
            this.page = page;
            this.size = size;
        }
        
        // getters and setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public Weather getWeather() { return weather; }
        public void setWeather(Weather weather) { this.weather = weather; }
        public Mood getMood() { return mood; }
        public void setMood(Mood mood) { this.mood = mood; }
        public boolean getIsPrivate() { return isPrivate; }
        public void setIsPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
    }
    
    // 페이징 응답용 DTO
    public static class PageResponse {
        private List<Response> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        
        public PageResponse() {}
        
        public PageResponse(List<Response> content, int page, int size, long totalElements, int totalPages) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
        
        // getters and setters
        public List<Response> getContent() { return content; }
        public void setContent(List<Response> content) { this.content = content; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
    
    // 통계용 DTO
    public static class StatisticsResponse {
        private Long totalDiaries;
        private Long diariesByWeather;
        private Long diariesByMood;
        private List<Weather> weatherList;
        private List<Mood> moodList;
        
        public StatisticsResponse() {}
        
        public StatisticsResponse(Long totalDiaries, Long diariesByWeather, Long diariesByMood, 
                               List<Weather> weatherList, List<Mood> moodList) {
            this.totalDiaries = totalDiaries;
            this.diariesByWeather = diariesByWeather;
            this.diariesByMood = diariesByMood;
            this.weatherList = weatherList;
            this.moodList = moodList;
        }
        
        // getters and setters
        public Long getTotalDiaries() { return totalDiaries; }
        public void setTotalDiaries(Long totalDiaries) { this.totalDiaries = totalDiaries; }
        public Long getDiariesByWeather() { return diariesByWeather; }
        public void setDiariesByWeather(Long diariesByWeather) { this.diariesByWeather = diariesByWeather; }
        public Long getDiariesByMood() { return diariesByMood; }
        public void setDiariesByMood(Long diariesByMood) { this.diariesByMood = diariesByMood; }
        public List<Weather> getWeatherList() { return weatherList; }
        public void setWeatherList(List<Weather> weatherList) { this.weatherList = weatherList; }
        public List<Mood> getMoodList() { return moodList; }
        public void setMoodList(List<Mood> moodList) { this.moodList = moodList; }
    }
} 