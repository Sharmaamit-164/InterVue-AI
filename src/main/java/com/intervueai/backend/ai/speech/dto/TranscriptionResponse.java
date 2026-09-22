package com.intervueai.backend.ai.speech.dto;

public class TranscriptionResponse {

    private String text;
    private Double confidence;
    private String language;
    private Double durationSeconds;
    private String provider;

    public TranscriptionResponse() {
    }

    public TranscriptionResponse(String text, Double confidence, String language, Double durationSeconds, String provider) {
        this.text = text;
        this.confidence = confidence;
        this.language = language;
        this.durationSeconds = durationSeconds;
        this.provider = provider;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Double getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Double durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
