package com.intervueai.backend.ai.avatar;

import com.intervueai.backend.ai.avatar.dto.AvatarSessionRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSessionResponse;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakResponse;

public interface AvatarProvider {

    /**
     * Identifies the provider implementation (e.g. "DID", "HEYGEN", "MOCK").
     */
    String getProviderName();

    /**
     * Initializes an interactive or streaming avatar session.
     */
    AvatarSessionResponse createSession(AvatarSessionRequest request);

    /**
     * Instructs the avatar to speak the provided text.
     */
    AvatarSpeakResponse speak(AvatarSpeakRequest request);

    /**
     * Closes the avatar streaming session and releases resources.
     */
    void closeSession(String sessionId);
}
