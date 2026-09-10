package com.intervueai.backend.evaluation.dto;

import java.time.LocalDateTime;

public class EvaluationResponse {

    private Long id;
    private Long interviewId;

    private Integer overallScore;
    private Integer technicalScore;
    private Integer communicationScore;
    private Integer problemSolvingScore;

    private String recommendation;

    private String strengths;
    private String weaknesses;
    private String feedback;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EvaluationResponse() {
    }

    public EvaluationResponse(
            Long id,
            Long interviewId,
            Integer overallScore,
            Integer technicalScore,
            Integer communicationScore,
            Integer problemSolvingScore,
            String recommendation,
            String strengths,
            String weaknesses,
            String feedback,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.interviewId = interviewId;
        this.overallScore = overallScore;
        this.technicalScore = technicalScore;
        this.communicationScore = communicationScore;
        this.problemSolvingScore = problemSolvingScore;
        this.recommendation = recommendation;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.feedback = feedback;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public Integer getTechnicalScore() {
        return technicalScore;
    }

    public Integer getCommunicationScore() {
        return communicationScore;
    }

    public Integer getProblemSolvingScore() {
        return problemSolvingScore;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getStrengths() {
        return strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public String getFeedback() {
        return feedback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}