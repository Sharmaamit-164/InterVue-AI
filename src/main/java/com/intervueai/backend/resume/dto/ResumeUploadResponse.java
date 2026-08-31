package com.intervueai.backend.resume.dto;

import java.time.LocalDateTime;

public class ResumeUploadResponse {

    private Long id;
    private String fileName;
    private String message;
    private LocalDateTime uploadedAt;

    public ResumeUploadResponse() {
    }

    public ResumeUploadResponse(
            Long id,
            String fileName,
            String message,
            LocalDateTime uploadedAt
    ) {
        this.id = id;
        this.fileName = fileName;
        this.message = message;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}