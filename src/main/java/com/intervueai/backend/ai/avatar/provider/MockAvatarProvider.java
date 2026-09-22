package com.intervueai.backend.ai.avatar.provider;

import com.intervueai.backend.ai.avatar.AvatarProvider;
import com.intervueai.backend.ai.avatar.dto.AvatarSessionRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSessionResponse;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MockAvatarProvider implements AvatarProvider {

    @Override
    public String getProviderName() {
        return "MOCK";
    }

    @Override
    public AvatarSessionResponse createSession(AvatarSessionRequest request) {
        String sessionId = "mock-avatar-session-" + UUID.randomUUID();
        return new AvatarSessionResponse(
                sessionId,
                "CONNECTED",
                "mock://stream/" + sessionId,
                "ws://localhost:8080/mock-avatar-ws",
                "mock-token-" + UUID.randomUUID(),
                getProviderName()
        );
    }

    @Override
    public AvatarSpeakResponse speak(AvatarSpeakRequest request) {
        String talkId = "talk-" + UUID.randomUUID();
        double estimatedDuration = Math.max(1.5, request.getText().split("\\s+").length * 0.35);
        return new AvatarSpeakResponse(
                talkId,
                "SUCCESS",
                null,
                null,
                estimatedDuration,
                request.getText()
        );
    }

    @Override
    public void closeSession(String sessionId) {
        // Mock cleanup
    }
}
