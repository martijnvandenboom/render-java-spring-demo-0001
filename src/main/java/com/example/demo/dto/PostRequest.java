package com.example.demo.dto;

public class PostRequest {
    private String title;
    private String subtitle;
    private String content;
    private String headerImageUrl;
    private boolean published;

    public PostRequest() {}

    public PostRequest(String title, String subtitle, String content, String headerImageUrl, boolean published) {
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.headerImageUrl = headerImageUrl;
        this.published = published;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeaderImageUrl() {
        return headerImageUrl;
    }

    public void setHeaderImageUrl(String headerImageUrl) {
        this.headerImageUrl = headerImageUrl;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
