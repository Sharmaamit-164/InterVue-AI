package com.intervueai.backend.job.entity;

import com.intervueai.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who created this job
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Job title
    @Column(nullable = false)
    private String title;

    // Company name
    @Column(nullable = false)
    private String companyName;

    // Complete job description
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // Required skills for the job
    @Column(columnDefinition = "TEXT")
    private String requiredSkills;

    // Date and time when job was created
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Date and time when job was last updated
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor required by JPA
    public Job() {
    }

    // Constructor
    public Job(
            User user,
            String title,
            String companyName,
            String description,
            String requiredSkills,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.user = user;
        this.title = title;
        this.companyName = companyName;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}