package com.intervueai.backend.ai.speech;

import com.intervueai.backend.ai.speech.dto.TranscriptionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SpeechServiceImpl implements SpeechService {

    private final Map<String, SpeechProvider> providers;
    private final String defaultProviderName;

    public SpeechServiceImpl(
            List<SpeechProvider> providerList,
            @Value("${speech.provider:mock}") String defaultProviderName
    ) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        p -> p.getProviderName().toUpperCase(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        this.defaultProviderName = defaultProviderName.toUpperCase();
    }

    private SpeechProvider getActiveProvider() {
        SpeechProvider provider = providers.get(defaultProviderName);
        if (provider == null) {
            provider = providers.get("MOCK");
        }
        if (provider == null && !providers.isEmpty()) {
            provider = providers.values().iterator().next();
        }
        if (provider == null) {
            throw new IllegalStateException("No speech provider configured");
        }
        return provider;
    }

    @Override
    public TranscriptionResponse transcribeAudio(MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("Audio file cannot be null or empty");
        }

        try {
            byte[] bytes = audioFile.getBytes();
            String contentType = audioFile.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "audio/webm";
            }
            return transcribeAudioBytes(bytes, contentType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read audio file bytes", e);
        }
    }

    @Override
    public TranscriptionResponse transcribeAudioBytes(byte[] audioBytes, String contentType) {
        if (audioBytes == null || audioBytes.length == 0) {
            throw new IllegalArgumentException("Audio bytes cannot be null or empty");
        }
        return getActiveProvider().transcribe(audioBytes, contentType);
    }
}
