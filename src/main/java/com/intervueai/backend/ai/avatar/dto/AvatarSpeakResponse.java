package com.intervueai.backend.ai.avatar.dto;

public class AvatarSpeakResponse {

    private String talkId;
    private String status;
    private String audioUrl;
    private String videoUrl;
    private Double durationSeconds;
    private String text;

    public AvatarSpeakResponse() {
    }

    public AvatarSpeakResponse(String talkId, String status, String audioUrl, String videoUrl, Double durationSeconds, String text) {
        this.talkId = talkId;
        this.status = status;
        this.audioUrl = audioUrl;
        this.videoUrl = videoUrl;
        this.durationSeconds = durationSeconds;
        this.text = text;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Double getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Double durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
