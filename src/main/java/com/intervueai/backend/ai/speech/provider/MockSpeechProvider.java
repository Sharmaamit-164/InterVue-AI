package com.intervueai.backend.ai.speech.provider;

import com.intervueai.backend.ai.speech.SpeechProvider;
import com.intervueai.backend.ai.speech.dto.TranscriptionResponse;
import org.springframework.stereotype.Component;

@Component
public class MockSpeechProvider implements SpeechProvider {

    @Override
    public String getProviderName() {
        return "MOCK";
    }

    @Override
    public TranscriptionResponse transcribe(byte[] audioBytes, String contentType) {
        return new TranscriptionResponse(
                "I have experience building RESTful web services using Java and Spring Boot with PostgreSQL.",
                0.98,
                "en",
                3.5,
                getProviderName()
        );
    }
}
