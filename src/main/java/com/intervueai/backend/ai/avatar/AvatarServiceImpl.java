package com.intervueai.backend.ai.avatar;

import com.intervueai.backend.ai.avatar.dto.AvatarSessionRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSessionResponse;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final Map<String, AvatarProvider> providers;
    private final String defaultProviderName;

    public AvatarServiceImpl(
            List<AvatarProvider> providerList,
            @Value("${avatar.provider:mock}") String defaultProviderName
    ) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        p -> p.getProviderName().toUpperCase(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        this.defaultProviderName = defaultProviderName.toUpperCase();
    }

    private AvatarProvider getActiveProvider() {
        AvatarProvider provider = providers.get(defaultProviderName);
        if (provider == null) {
            provider = providers.get("MOCK");
        }
        if (provider == null && !providers.isEmpty()) {
            provider = providers.values().iterator().next();
        }
        if (provider == null) {
            throw new IllegalStateException("No avatar provider configured");
        }
        return provider;
    }

    @Override
    public AvatarSessionResponse startSession(AvatarSessionRequest request) {
        return getActiveProvider().createSession(request);
    }

    @Override
    public AvatarSpeakResponse speak(AvatarSpeakRequest request) {
        if (request == null || request.getText() == null || request.getText().isBlank()) {
            throw new IllegalArgumentException("Text to speak cannot be empty");
        }
        return getActiveProvider().speak(request);
    }

    @Override
    public void endSession(String sessionId) {
        if (sessionId != null) {
            getActiveProvider().closeSession(sessionId);
        }
    }
}
