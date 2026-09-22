package com.intervueai.backend.ai.avatar.dto;

public class AvatarSessionRequest {

    private String avatarId;
    private String voiceId;
    private String quality;

    public AvatarSessionRequest() {
    }

    public AvatarSessionRequest(String avatarId, String voiceId, String quality) {
        this.avatarId = avatarId;
        this.voiceId = voiceId;
        this.quality = quality;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
