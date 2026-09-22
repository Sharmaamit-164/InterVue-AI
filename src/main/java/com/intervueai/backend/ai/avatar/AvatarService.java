package com.intervueai.backend.ai.avatar;

import com.intervueai.backend.ai.avatar.dto.AvatarSessionRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSessionResponse;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakResponse;

public interface AvatarService {

    /**
     * Initializes an interactive avatar session.
     */
    AvatarSessionResponse startSession(AvatarSessionRequest request);

    /**
     * Sends dialogue to the avatar to speak.
     */
    AvatarSpeakResponse speak(AvatarSpeakRequest request);

    /**
     * Closes the avatar session.
     */
    void endSession(String sessionId);
}
