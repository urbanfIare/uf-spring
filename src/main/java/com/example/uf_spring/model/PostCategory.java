package com.example.uf_spring.model;

public enum PostCategory {
    GENERAL("일반"),
    NOTICE("공지사항"),
    QNA("질문과답변"),
    FREE("자유게시판"),
    TECH("기술"),
    NEWS("뉴스");
    
    private final String displayName;
    
    PostCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 