package com.intervueai.backend.ai.llm;

import org.springframework.stereotype.Service;

@Service
public class LLMServiceImpl implements LLMService {

    private final LLMProvider llmProvider;

    public LLMServiceImpl(LLMProvider llmProvider) {
        this.llmProvider = llmProvider;
    }

    @Override
    public String generateResponse(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Prompt cannot be null or empty"
            );
        }

        String response = llmProvider.generate(prompt);

        if (response == null || response.isBlank()) {
            throw new RuntimeException(
                    "LLM returned an empty response"
            );
        }

        return response.trim();
    }
}