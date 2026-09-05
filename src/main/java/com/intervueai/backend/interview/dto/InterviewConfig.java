package com.intervueai.backend.interview.dto;

public class InterviewConfig {

    private int maxQuestions;
    private int durationMinutes;
    private String interviewType;
    private String difficulty;

    public InterviewConfig() {
    }

    public InterviewConfig(
            int maxQuestions,
            int durationMinutes,
            String interviewType,
            String difficulty
    ) {
        this.maxQuestions = maxQuestions;
        this.durationMinutes = durationMinutes;
        this.interviewType = interviewType;
        this.difficulty = difficulty;
    }

    public int getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(int maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(String interviewType) {
        this.interviewType = interviewType;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}