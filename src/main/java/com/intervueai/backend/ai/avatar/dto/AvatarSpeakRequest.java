package com.intervueai.backend.ai.avatar.dto;

import jakarta.validation.constraints.NotBlank;

public class AvatarSpeakRequest {

    private String sessionId;

    @NotBlank(message = "Text to speak cannot be empty")
    private String text;

    private String taskType;

    public AvatarSpeakRequest() {
    }

    public AvatarSpeakRequest(String sessionId, String text, String taskType) {
        this.sessionId = sessionId;
        this.text = text;
        this.taskType = taskType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}
