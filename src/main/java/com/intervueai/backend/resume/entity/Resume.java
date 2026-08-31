package com.intervueai.backend.resume.entity;

import com.intervueai.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who owns this resume
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Original name of the uploaded resume
    @Column(nullable = false)
    private String fileName;

    // File type, for example: application/pdf
    @Column(nullable = false)
    private String fileType;

    // Actual PDF file stored in PostgreSQL
    @Lob
    @Column(nullable = false)
    private byte[] fileData;

    // Text extracted from the resume PDF
    @Column(columnDefinition = "TEXT")
    private String parsedText;

    // Date and time when the resume was uploaded
    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    // Default constructor required by JPA
    public Resume() {
    }

    // Constructor
    public Resume(
            User user,
            String fileName,
            String fileType,
            byte[] fileData,
            String parsedText,
            LocalDateTime uploadedAt
    ) {
        this.user = user;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
        this.parsedText = parsedText;
        this.uploadedAt = uploadedAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
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