package com.intervueai.backend.ai.avatar.dto;

public class AvatarSessionResponse {

    private String sessionId;
    private String status;
    private String streamUrl;
    private String wsUrl;
    private String sessionToken;
    private String provider;

    public AvatarSessionResponse() {
    }

    public AvatarSessionResponse(String sessionId, String status, String streamUrl, String wsUrl, String sessionToken, String provider) {
        this.sessionId = sessionId;
        this.status = status;
        this.streamUrl = streamUrl;
        this.wsUrl = wsUrl;
        this.sessionToken = sessionToken;
        this.provider = provider;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
