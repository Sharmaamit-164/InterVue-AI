package com.intervueai.backend.resume.dto;

import java.time.LocalDateTime;

public class ResumeResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private String parsedText;
    private LocalDateTime uploadedAt;

    public ResumeResponse() {
    }

    public ResumeResponse(
            Long id,
            String fileName,
            String fileType,
            String parsedText,
            LocalDateTime uploadedAt
    ) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.parsedText = parsedText;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getParsedText() {
        return parsedText;
    }

    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}