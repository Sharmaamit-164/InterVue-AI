package com.intervueai.backend.interview.dto;

import java.time.LocalDateTime;

public class InterviewResponse {

    private Long id;
    private Long resumeId;
    private Long jobId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public InterviewResponse() {
    }

    public InterviewResponse(
            Long id,
            Long resumeId,
            Long jobId,
            String status,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        this.id = id;
        this.resumeId = resumeId;
        this.jobId = jobId;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
}